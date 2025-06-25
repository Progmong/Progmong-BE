package com.progmong.api.explore.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record ProblemRecordListQueryDto(
        List<ProblemRecordQueryDto> records,
        long totalCount
) {
    public static ProblemRecordListQueryDto of(List<ProblemRecordQueryDto> records, long totalCount) {
        return ProblemRecordListQueryDto.builder()
                .records(records)
                .totalCount(totalCount)
                .build();
    }
}
