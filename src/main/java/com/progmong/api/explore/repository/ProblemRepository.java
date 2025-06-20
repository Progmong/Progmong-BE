package com.progmong.api.explore.repository;


import com.progmong.api.explore.entity.Problem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
}
