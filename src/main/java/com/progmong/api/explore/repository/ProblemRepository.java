package com.progmong.api.explore.repository;


import com.progmong.api.explore.entity.Problem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProblemRepository extends JpaRepository<Problem, Long> {
    @Query(value = """
            SELECT * FROM problem p
            WHERE p.main_tag IN (:tags)
              AND p.level BETWEEN :minLevel AND :maxLevel
              AND p.problem_id NOT IN (
                  SELECT sp.problem_id FROM solved_problem sp WHERE sp.user_id = :userId
              )
            ORDER BY RAND()
            LIMIT 5
            """, nativeQuery = true)
    List<Problem> findRandomUnsolvedProblemsByTagsAndLevel(
            @Param("tags") List<String> tags,
            @Param("minLevel") int minLevel,
            @Param("maxLevel") int maxLevel,
            @Param("userId") Long userId
    );

    @Query(value = """
    SELECT * FROM problem p
    WHERE p.level BETWEEN :minLevel AND :maxLevel
      AND p.problem_id NOT IN (
          SELECT sp.problem_id FROM solved_problem sp WHERE sp.user_id = :userId
      )
    ORDER BY RAND()
    LIMIT 5
    """, nativeQuery = true)
    List<Problem> findRandomUnsolvedProblemsByLevelOnly(
            @Param("minLevel") int minLevel,
            @Param("maxLevel") int maxLevel,
            @Param("userId") Long userId
    );
}
