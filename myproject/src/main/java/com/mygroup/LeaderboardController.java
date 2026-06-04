package com.mygroup;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/leaderboard")
public class LeaderboardController {

    private static final int MAX_SCORE = 300;
    private static final int MAX_MOVES = 10000;
    private static final long RATE_LIMIT_MS = 60_000;

    private final ScoreRepository repo;
    private final ConcurrentHashMap<String, Long> lastSaveTime = new ConcurrentHashMap<>();

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

    private String getClientIp(HttpServletRequest request) {
        String forwarded = request.getHeader("X-Forwarded-For");
        return (forwarded != null) ? forwarded.split(",")[0].trim() : request.getRemoteAddr();
    }

    @PostMapping("/save")
    public ResponseEntity<Void> save(@RequestBody Map<String, Object> body, HttpServletRequest request) {
        try {
            String ip = getClientIp(request);
            long now = System.currentTimeMillis();
            Long last = lastSaveTime.get(ip);
            if (last != null && now - last < RATE_LIMIT_MS) return ResponseEntity.ok().build();

            String callsign = (String) body.getOrDefault("callsign", "OPERATOR_01");
            if (hasProfanity(callsign)) return ResponseEntity.ok().build();

            int score = ((Number) body.getOrDefault("score", 0)).intValue();
            int moveCount = ((Number) body.getOrDefault("moveCount", 0)).intValue();
            if (score < 0 || score > MAX_SCORE || moveCount < 1 || moveCount > MAX_MOVES)
                return ResponseEntity.ok().build();

            lastSaveTime.put(ip, now);
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
