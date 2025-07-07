package com.progmong.api.community.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CommunityActResDto {
    private Long postCount;
    private Long commentCount;
}
