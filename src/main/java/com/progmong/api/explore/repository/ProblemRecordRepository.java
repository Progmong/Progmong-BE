package com.progmong.api.explore.repository;

import com.progmong.api.explore.entity.ProblemRecord;
import com.progmong.api.user.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRecordRepository extends JpaRepository<ProblemRecord, Long> {

    Page<ProblemRecord> findByUser(User user, Pageable pageable);
    long countByUserId(Long userId);
    Optional<ProblemRecord> findByUserIdAndProblemId(Long userId, Long problemId);

}
