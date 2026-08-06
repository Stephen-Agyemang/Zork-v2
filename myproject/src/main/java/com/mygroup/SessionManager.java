package com.mygroup;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds all active game sessions in memory.
 * Each player gets a UUID session key that maps to their own GameEngine instance,
 * so multiple players can run independent games on the same server simultaneously.
 * ConcurrentHashMap is used because multiple HTTP threads may read/write sessions at once.
 * Sessions live until the player quits (removeSession()), the server restarts, or they
 * go idle past the TTL and are reclaimed by the scheduled evictIdleSessions() sweep.
 */
@Component
public class SessionManager {

    // sessionId → that player's game engine (one engine = one full game world)
    private final ConcurrentHashMap<String, GameEngine> sessions = new ConcurrentHashMap<>();

    // sessionId → epoch-millis of the last time this session was touched (created or read).
    // Drives idle eviction so abandoned games (tab closed without quitting) don't hold a
    // slot forever. Kept in lock-step with `sessions` by createSession/getEngine/removeSession.
    private final ConcurrentHashMap<String, Long> lastAccess = new ConcurrentHashMap<>();

    // Hard cap to prevent memory exhaustion on the server
    private static final int MAX_SESSIONS = 400;

    // A session untouched for this long is treated as abandoned and swept. Defaults to
    // 60 minutes — deliberately generous: a player who steps away mid-game keeps their
    // world, and any tab still open keeps refreshing its timestamp via the HUD's
    // /game/state polling. Overridable via the session.idle-ttl-ms property (mainly so
    // tests can use a short TTL instead of waiting an hour).
    @Value("${session.idle-ttl-ms:3600000}")
    private long idleTtlMs;

    // Slots currently reserved. Claimed BEFORE a session is built and released when
    // one is removed (or when a create is refused). The cap is enforced on this
    // counter rather than on sessions.size() because check-then-put on the map is
    // two steps: under a concurrent burst several threads could each pass a
    // size() < MAX_SESSIONS check before any of them put(), collectively pushing the
    // live session count past the cap (observed overshooting to 403 in load tests).
    private final AtomicInteger reservedSlots = new AtomicInteger(0);

    // Creates a new session, builds the world, sets the player name, and returns the session ID.
    // Returns null if the server is at capacity.
    public String createSession(String callsign) {
        // Atomically claim a slot; if claiming it would exceed the cap, hand it back
        // and refuse. incrementAndGet + the compensating decrement make the capacity
        // check and the reservation a single atomic operation, so concurrent starts
        // can never overshoot MAX_SESSIONS.
        if (reservedSlots.incrementAndGet() > MAX_SESSIONS) {
            reservedSlots.decrementAndGet();
            return null;
        }
        String sessionId = UUID.randomUUID().toString();
        GameEngine engine = new GameEngine();
        engine.createWorld();
        engine.getGameState().setPlayerName(callsign != null && !callsign.isBlank() ? callsign.trim() : "OPERATOR_01");
        sessions.put(sessionId, engine);
        lastAccess.put(sessionId, System.currentTimeMillis());
        return sessionId;
    }

    public GameEngine getEngine(String sessionId) {
        if (sessionId == null) return null;
        GameEngine engine = sessions.get(sessionId);
        // Every command and HUD poll flows through here, so refreshing the timestamp
        // on a hit is all the "keep-alive" an active player needs to avoid eviction.
        if (engine != null) {
            lastAccess.put(sessionId, System.currentTimeMillis());
        }
        return engine;
    }

    public void removeSession(String sessionId) {
        // Only release a slot if we actually removed a live session, so double
        // removes (or unknown ids) can't drive reservedSlots below the real count.
        if (sessionId != null && sessions.remove(sessionId) != null) {
            lastAccess.remove(sessionId);
            reservedSlots.decrementAndGet();
        }
    }

    // Reclaims slots held by sessions that have gone idle past the TTL. Without this, a
    // player who closes the tab without quitting would hold a slot until the next server
    // restart, so on an always-on host abandoned games would slowly fill the cap and start
    // turning real players away. Runs every 5 minutes by default (overridable via
    // session.sweep-interval-ms); @EnableScheduling is on App.
    @Scheduled(fixedRateString = "${session.sweep-interval-ms:300000}")
    public void evictIdleSessions() {
        long cutoff = System.currentTimeMillis() - idleTtlMs;
        lastAccess.forEach((sessionId, lastTouch) -> {
            if (lastTouch < cutoff) {
                // Re-read under the current state: if activity refreshed the timestamp
                // between the scan and now, leave the live player alone.
                Long current = lastAccess.get(sessionId);
                if (current != null && current < cutoff) {
                    removeSession(sessionId);
                }
            }
        });
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
