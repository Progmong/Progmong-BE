package com.progmong.api.community.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
public class PostListElementResDto {
    private String title;
    private Long postId;
    private Long userId;
    private String nickname;
    private int likeCount;
    private LocalDateTime createdAt;
}
