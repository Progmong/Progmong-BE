package com.progmong.api.explore.repository;

import com.progmong.api.explore.entity.RecommendProblem;
import com.progmong.api.explore.entity.RecommendStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendProblemRepository extends JpaRepository<RecommendProblem,Long> {
    Optional<RecommendProblem> findByUserIdAndStatus(Long userId, RecommendStatus recommendStatus);
    Optional<RecommendProblem> findByUserIdAndSequence(Long userId,int sequence);
    List<RecommendProblem> findAllByUserIdOrderBySequence(Long userId);

    void deleteAllByUserId(Long userId);
}
