package com.progmong.api.explore.dto;

import java.util.List;
import lombok.Builder;

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
