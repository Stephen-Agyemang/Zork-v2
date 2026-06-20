package com.mygroup;

import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

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

    // Creates a new session, builds the world, sets the player name, and returns the session ID.
    // Returns null if the server is at capacity.
    public String createSession(String callsign) {
        if (sessions.size() >= MAX_SESSIONS) {
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
        sessions.remove(sessionId);
    }

    public int getActiveSessionCount() {
        return sessions.size();
    }
}
