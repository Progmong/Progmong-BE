package com.progmong.api.community.repository;

import com.progmong.api.community.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Long countByUserId(Long userId);

    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);
}
