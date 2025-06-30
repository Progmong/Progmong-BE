package com.progmong.api.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendProblemListResponseDto2 {
    private List<RecommendProblemResponseDto> recommendProblems;
    private boolean isFinish;
    private Integer totalExp; // 종료 시만 값 존재, 아니면 null
    private int pageCount;
}
