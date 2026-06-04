package com.mygroup;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private final ScoreRepository repo;

    public LeaderboardController(ScoreRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody Map<String, Object> body) {
        try {
            String callsign = (String) body.getOrDefault("callsign", "OPERATOR_01");
            int score = ((Number) body.getOrDefault("score", 0)).intValue();
            int moveCount = ((Number) body.getOrDefault("moveCount", 0)).intValue();
            repo.save(new ScoreEntry(callsign, score, moveCount));
        } catch (Exception e) {
            // fail silently if DB unavailable
        }
        return ResponseEntity.ok().build();
    }

    @GetMapping("/top")
    public ResponseEntity<List<ScoreEntry>> top() {
        try {
            return ResponseEntity.ok(repo.findTop10ByOrderByScoreDescMoveCountAsc());
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}
