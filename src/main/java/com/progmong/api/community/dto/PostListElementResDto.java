package com.progmong.api.community.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
    private LocalDateTime createdAt;
}
