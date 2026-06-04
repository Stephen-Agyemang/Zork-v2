package com.mygroup;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "scores")
public class ScoreEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String callsign;
    private int score;
    private int moveCount;
    private LocalDateTime completedAt;

    public ScoreEntry() {}

    public ScoreEntry(String callsign, int score, int moveCount) {
        this.callsign = callsign;
        this.score = score;
        this.moveCount = moveCount;
        this.completedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getCallsign() { return callsign; }
    public int getScore() { return score; }
    public int getMoveCount() { return moveCount; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
