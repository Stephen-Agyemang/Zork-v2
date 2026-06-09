package com.mygroup;

import org.springframework.stereotype.Component;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SessionManager {

    private final ConcurrentHashMap<String, GameEngine> sessions = new ConcurrentHashMap<>();

    private static final int MAX_SESSIONS = 400;

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
