package com.progmong.api.explore.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * 탐험 기록 응답 DTO
 * - 페이지 정보와 탐험 완료 여부, 총 경험치 정보를 포함
 */
public class ExploreRecordsResponse {
    private PagedResponseDto<RecommendProblemResponseDto> pageInfo;
    private boolean finish;
    private Integer totalExp;
}
