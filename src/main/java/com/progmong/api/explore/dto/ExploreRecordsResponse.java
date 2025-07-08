package com.progmong.api.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
// 탐험 기록 응답 DTO
// - PagedResponseDto<RecommendProblemResponseDto> pageInfo: 페이지 정보와 문제 목록
public class ExploreRecordsResponse {
    private PagedResponseDto<RecommendProblemResponseDto> pageInfo;
    private boolean finish;
    private Integer totalExp;
}
