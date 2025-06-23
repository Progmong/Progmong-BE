package com.progmong.api.explore.repository;

import com.progmong.api.explore.entity.ProblemRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProblemRecordRepository extends JpaRepository<ProblemRecord,Long> {
    Optional<ProblemRecord> findByUserIdAndProblemId(Long userId, Long problemId);
}
