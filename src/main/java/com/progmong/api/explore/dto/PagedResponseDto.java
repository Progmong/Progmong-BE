package com.progmong.api.explore.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * 페이지 응답 DTO
 * - 제네릭 타입 T를 사용하여 다양한 데이터 타입을 지원이 가능하나 해당 프로젝트에서는 탐험 기록 조회에서만 사용
 * - 페이지 정보와 총 요소 수, 페이지 상태 등을 포함
 */
public class PagedResponseDto<T> {
    private List<T> content;
    private int page;
    private int size;
    private int totalPages;
    private long totalElements;
    private boolean hasNext;
    private boolean isFirst;
    private boolean isLast;
}
