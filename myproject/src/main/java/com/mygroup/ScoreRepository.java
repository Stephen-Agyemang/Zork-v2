package com.mygroup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for ScoreEntry.
 * Spring auto-generates the SQL queries at startup from the method names — no implementation needed.
 * findTop10By...  → SELECT TOP 10 ordered by score DESC, then move count ASC (fewest moves wins ties)
 * findAllBy...    → same ordering but no row limit (used for DPU-filtered leaderboard)
 * findTopByCallsign... → best existing entry for a given callsign (used for deduplication)
 */
public interface ScoreRepository extends JpaRepository<ScoreEntry, Long> {
    List<ScoreEntry> findTop10ByOrderByScoreDescMoveCountAsc();
    List<ScoreEntry> findAllByOrderByScoreDescMoveCountAsc();
    Optional<ScoreEntry> findTopByCallsignIgnoreCaseOrderByScoreDescMoveCountAsc(String callsign);
}
