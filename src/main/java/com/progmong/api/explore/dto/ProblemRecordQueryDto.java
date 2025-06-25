package com.progmong.api.explore.dto;

import com.progmong.api.explore.entity.ProblemRecord;
import com.progmong.api.explore.entity.Result;
import lombok.Builder;

@Builder
public record ProblemRecordQueryDto(
        Long recordId,
        Long problemId,
        String title,
        int level,
        String mainTag,
        Result result
) {
    public static ProblemRecordQueryDto fromEntity(ProblemRecord entity) {
        return ProblemRecordQueryDto.builder()
                .recordId(entity.getId())
                .problemId(entity.getProblem().getId())
                .title(entity.getProblem().getTitle())
                .level(entity.getProblem().getLevel())
                .mainTag(entity.getProblem().getMainTag())
                .result(entity.getResult())
                .build();
    }
}
