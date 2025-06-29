package com.progmong.api.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class PostDetailResDto {
    private Long postId;
    private Long userId;
    private String title;
    private String nickname;
    private String content;
    private int likeCount;
    private LocalDateTime createdAt;
}
