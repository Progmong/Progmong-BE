package com.progmong.api.explore.repository;

import com.progmong.api.explore.entity.SolvedProblem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SolvedProblemRepository extends JpaRepository<SolvedProblem,Long> {
    boolean existsByUserIdAndProblemId(Long userId, Long problemId);
}
