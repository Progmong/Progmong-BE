package com.progmong.api.tag.repository;


import com.progmong.api.tag.entity.InterestTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InterestTagRepository  extends JpaRepository<InterestTag, Long> {
    List<InterestTag> findByUserId(Long userId);
    void deleteByUserIdAndTagIdIn(Long userId, List<Long> tagIds);
    void deleteByUserId(Long userId);
    boolean existsByUserIdAndTagId(Long userId, Long tagId);
}