package com.mygroup;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for leaderboard score submission and retrieval.
 *
 * POST /leaderboard/save    — upsert: keeps only the best score per callsign
 * GET  /leaderboard/top     — top 10 global scores (highest score, fewest moves wins ties)
 * GET  /leaderboard/top-dpu — top 10 filtered to callsigns ending in _dpu or .dpu
 *
 * Defenses:
 *  - Score bounds: rejects impossible scores and bogus move counts
 *  - Profanity filter: silently drops entries with blocked words in the callsign
 *  - One row per callsign: only replaces existing entry if new score is strictly better
 */
@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private static final int MAX_SCORE = 300;
    private static final int MAX_MOVES = 10000;

    private final ScoreRepository repo;

    public LeaderboardController(ScoreRepository repo) {
        this.repo = repo;
    }

    private static final String[] BLOCKED = {
        "fuck","fuk","fvck","shit","sht","ass","arse","bitch","cunt","dick",
        "cock","pussy","nigger","nigga","faggot","fag","bastard","whore",
        "slut","retard","piss","prick","twat","wanker","bollocks"
    };

    private boolean hasProfanity(String text) {
        String lower = text.toLowerCase().replaceAll("[^a-z0-9]", "");
        for (String word : BLOCKED) {
            if (lower.contains(word)) return true;
        }
        return false;
    }

    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody Map<String, Object> body) {
        try {
            String callsign = (String) body.getOrDefault("callsign", "OPERATOR_01");
            if (hasProfanity(callsign)) return ResponseEntity.ok().build();

            int score = ((Number) body.getOrDefault("score", 0)).intValue();
            int moveCount = ((Number) body.getOrDefault("moveCount", 0)).intValue();
            if (score < 0 || score > MAX_SCORE || moveCount < 1 || moveCount > MAX_MOVES)
                return ResponseEntity.ok().build();

            Optional<ScoreEntry> existing = repo.findTopByCallsignIgnoreCaseOrderByScoreDescMoveCountAsc(callsign);
            if (existing.isPresent()) {
                ScoreEntry best = existing.get();
                boolean newIsBetter = score > best.getScore()
                        || (score == best.getScore() && moveCount < best.getMoveCount());
                if (newIsBetter) {
                    repo.delete(best);
                    repo.save(new ScoreEntry(callsign, score, moveCount));
                }
            } else {
                repo.save(new ScoreEntry(callsign, score, moveCount));
            }
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

    @GetMapping("/top-dpu")
    public ResponseEntity<List<ScoreEntry>> topDpu() {
        try {
            List<ScoreEntry> dpu = repo.findAllByOrderByScoreDescMoveCountAsc().stream()
                .filter(e -> {
                    String name = e.getCallsign().toLowerCase();
                    return name.endsWith("_dpu") || name.endsWith(".dpu");
                })
                .limit(10)
                .collect(Collectors.toList());
            return ResponseEntity.ok(dpu);
        } catch (Exception e) {
            return ResponseEntity.ok(List.of());
        }
    }
}
