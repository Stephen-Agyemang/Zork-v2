package com.mygroup;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

/**
 * REST controller for all game actions.
 * Every player browser tab is its own session, identified by the X-Session-ID header
 * that the frontend stores in localStorage after calling /game/start.
 *
 * Endpoints:
 *   POST /game/start   — create a new game session and return a session ID
 *   POST /game/command — send a typed command; returns the game's text response
 *   GET  /game/state   — poll the current GameState (moves, points, quest flags, etc.)
 */
@RestController
@RequestMapping("/game")
public class GameController {

    private final SessionManager sessionManager;

    public GameController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // Creates a fresh game world for the player and returns a UUID session token
    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startGame(@RequestBody Map<String, String> body) {
        String callsign = body.getOrDefault("callsign", "OPERATOR_01");
        String sessionId = sessionManager.createSession(callsign);
        if (sessionId == null) {
            // MAX_SESSIONS cap hit — tell the client to retry rather than silently failing
            return ResponseEntity.status(503).body(Map.of("message", "Server is at capacity. Try again shortly."));
        }
        return ResponseEntity.ok(Map.of(
            "sessionId", sessionId,
            "message", "Game world created, ready to play!"
        ));
    }

    // Routes a single player command through the game engine and returns the narrative response
    @PostMapping("/command")
    public ResponseEntity<String> processCommand(
            @RequestHeader("X-Session-ID") String sessionId,
            @RequestBody String userInput) {
        GameEngine engine = sessionManager.getEngine(sessionId);
        if (engine == null) {
            return ResponseEntity.status(404).body("Session not found. Please start a new game.");
        }
        return ResponseEntity.ok(engine.processCommand(userInput));
    }

    // Returns the full GameState as JSON so the frontend can update the HUD without a command
    @GetMapping("/state")
    public ResponseEntity<GameState> getGameState(@RequestHeader("X-Session-ID") String sessionId) {
        GameEngine engine = sessionManager.getEngine(sessionId);
        if (engine == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(engine.getGameState());
    }

}
