package com.progmong.api.tag.repository;


import com.progmong.api.tag.entity.InterestTag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestTagRepository extends JpaRepository<InterestTag, Long> {
    List<InterestTag> findByUserId(Long userId);

    void deleteByUserIdAndTagIdIn(Long userId, List<Long> tagIds);

    void deleteByUserId(Long userId);

    boolean existsByUserIdAndTagId(Long userId, Long tagId);
}
