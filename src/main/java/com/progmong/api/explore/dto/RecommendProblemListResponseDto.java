package com.progmong.api.explore.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendProblemListResponseDto {
    private List<RecommendProblemResponseDto> recommendProblems;
    private boolean isFinish;
    private Integer totalExp; // 종료 시만 값 존재, 아니면 null
}
