package com.mygroup;

import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * REST controller for leaderboard score submission and retrieval.
 *
 * POST /leaderboard/save    — server-verified upsert from X-Session-ID; keeps only best score per callsign
 * GET  /leaderboard/top     — top 10 global scores (highest score, fewest moves wins ties)
 * GET  /leaderboard/top-dpu — top 10 filtered to callsigns ending in _dpu or .dpu
 *
 * Defenses:
 *  - Session-bound saves: ignores browser-submitted score data and reads from server GameState
 *  - Score bounds: rejects impossible scores and bogus move counts
 *  - Mission gate: requires at least one completed mission and an ended run
 *  - Profanity filter: silently drops entries with blocked words in the callsign
 *  - One row per callsign: only replaces existing entry if new score is strictly better
 */
@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private static final Logger log = LoggerFactory.getLogger(LeaderboardController.class);
    private static final int MAX_SCORE = 300;
    private static final int MAX_MOVES = 10000;

    private final ScoreRepository repo;
    private final SessionManager sessionManager;

    public LeaderboardController(ScoreRepository repo, SessionManager sessionManager) {
        this.repo = repo;
        this.sessionManager = sessionManager;
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
    public ResponseEntity<String> save(@RequestHeader("X-Session-ID") String sessionId) {
        try {
            GameEngine engine = sessionManager.getEngine(sessionId);
            if (engine == null) return ResponseEntity.status(401).body("Invalid game session");

            GameState state = engine.getGameState();
            if (!state.isFinaleShown()) return ResponseEntity.status(409).body("Run has not ended");

            int completedMissions = completedMissionCount(state);
            if (completedMissions < 1) return ResponseEntity.ok().build();

            String callsign = state.getPlayerName();
            if (callsign == null || callsign.isBlank()) callsign = "OPERATOR_01";
            if (hasProfanity(callsign)) return ResponseEntity.ok().build();

            int score = state.getPoints();
            int moveCount = state.getMoveCount();
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
        } catch (RuntimeException e) {
            log.error("Leaderboard save failed", e);
            return ResponseEntity.status(503).body("Leaderboard database is unavailable");
        }
        return ResponseEntity.ok().build();
    }

    private int completedMissionCount(GameState state) {
        int count = 0;
        if (state.isMusicTaskComplete()) count++;
        if (state.isDnaTaskComplete()) count++;
        if (state.isSalmonTaskComplete()) count++;
        if (state.isSnakeTaskComplete()) count++;
        if (state.isMacbookTaskComplete()) count++;
        if (state.isTreadmillUsed()) count++;
        if (state.isArtifactTaskComplete()) count++;
        if (state.isStadiumTaskComplete()) count++;
        return count;
    }

    @GetMapping("/top")
    public ResponseEntity<List<ScoreEntry>> top() {
        try {
            return ResponseEntity.ok(repo.findTop10ByOrderByScoreDescMoveCountAsc());
        } catch (RuntimeException e) {
            log.error("Leaderboard read failed", e);
            return ResponseEntity.status(503).body(List.<ScoreEntry>of());
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
        } catch (RuntimeException e) {
            log.error("DPU leaderboard read failed", e);
            return ResponseEntity.status(503).body(List.<ScoreEntry>of());
        }
    }
}
