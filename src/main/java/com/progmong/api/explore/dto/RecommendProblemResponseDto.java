package com.progmong.api.explore.dto;

import com.progmong.api.explore.entity.RecommendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendProblemResponseDto {
    private Long id;
    private String title;
    private int level;
    private String mainTag;
    private int solvedUserCount;
    private RecommendStatus status;
    private int sequence;
}
