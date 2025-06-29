package com.progmong.api.community.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@Builder
@ToString
public class PostDetailResDto {
    private String title;
    private String content;
    private LocalDateTime createAt;
    private int likeCount;
}
