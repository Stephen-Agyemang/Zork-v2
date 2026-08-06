package com.mygroup;

import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Holds all active game sessions in memory.
 * Each player gets a UUID session key that maps to their own GameEngine instance,
 * so multiple players can run independent games on the same server simultaneously.
 * ConcurrentHashMap is used because multiple HTTP threads may read/write sessions at once.
 * Sessions are never evicted automatically — they live until the server restarts or
 * removeSession() is called.
 */
@Component
public class SessionManager {

    // sessionId → that player's game engine (one engine = one full game world)
    private final ConcurrentHashMap<String, GameEngine> sessions = new ConcurrentHashMap<>();

    // Hard cap to prevent memory exhaustion on the server
    private static final int MAX_SESSIONS = 400;

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
        return sessionId;
    }

    public GameEngine getEngine(String sessionId) {
        if (sessionId == null) return null;
        return sessions.get(sessionId);
    }

    public void removeSession(String sessionId) {
        // Only release a slot if we actually removed a live session, so double
        // removes (or unknown ids) can't drive reservedSlots below the real count.
        if (sessionId != null && sessions.remove(sessionId) != null) {
            reservedSlots.decrementAndGet();
        }
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
