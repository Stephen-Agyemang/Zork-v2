package com.mygroup;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * JPA entity that maps one completed game run to a row in the "scores" database table.
 * Spring/Hibernate creates and manages the table automatically via the @Entity annotation.
 */
@Entity
@Table(name = "scores")
public class ScoreEntry {

    // Auto-incremented primary key managed by the database
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String callsign;   // Player's chosen display name
    private int score;         // Final point total
    private int moveCount;     // Total moves used during the run
    private LocalDateTime completedAt; // Timestamp of when the score was submitted

    // Required by JPA — do not remove
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
