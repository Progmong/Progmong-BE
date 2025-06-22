package com.progmong.api.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class RecommendProblemListResponseDto {
    private List<RecommendProblemResponseDto> recommendProblems;
}
