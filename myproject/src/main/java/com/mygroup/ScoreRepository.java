package com.mygroup;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ScoreRepository extends JpaRepository<ScoreEntry, Long> {
    List<ScoreEntry> findTop10ByOrderByScoreDescMoveCountAsc();
}
