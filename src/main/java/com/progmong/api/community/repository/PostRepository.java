package com.progmong.api.community.repository;

import com.progmong.api.community.entity.Post;
import com.progmong.api.community.entity.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByOrderByCreatedAtDesc();

    Long countByUserId(Long userId);

    List<Post> findAllByCategoryOrderByCreatedAtDesc(PostCategory category);
}
