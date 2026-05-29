package com.mygroup;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import java.util.Map;

@RestController
@RequestMapping("/game")
public class GameController {

    private final SessionManager sessionManager;

    public GameController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @PostMapping("/start")
    public ResponseEntity<Map<String, String>> startGame(@RequestBody Map<String, String> body) {
        String callsign = body.getOrDefault("callsign", "OPERATOR_01");
        String sessionId = sessionManager.createSession(callsign);
        return ResponseEntity.ok(Map.of(
            "sessionId", sessionId,
            "message", "Game world created, ready to play!"
        ));
    }

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

    @GetMapping("/state")
    public ResponseEntity<GameState> getGameState(@RequestHeader("X-Session-ID") String sessionId) {
        GameEngine engine = sessionManager.getEngine(sessionId);
        if (engine == null) {
            return ResponseEntity.status(404).build();
        }
        return ResponseEntity.ok(engine.getGameState());
    }

    @PostMapping("/debug")
    public ResponseEntity<String> setDebugLogging(
            @RequestHeader(value = "X-Session-ID", required = false) String sessionId,
            @RequestBody boolean enabled) {
        if (sessionId != null) {
            GameEngine engine = sessionManager.getEngine(sessionId);
            if (engine != null) engine.setDebugLogging(enabled);
        }
        return ResponseEntity.ok("Debug logging " + (enabled ? "enabled." : "disabled."));
    }
}
