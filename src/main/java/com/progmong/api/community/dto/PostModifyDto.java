package com.progmong.api.community.dto;

import lombok.Builder;
import lombok.Getter;

@Getter @Builder
public class PostModifyDto {
    private Long postId;
    private String title;
    private String content;
}
