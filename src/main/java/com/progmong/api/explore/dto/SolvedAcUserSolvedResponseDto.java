package com.progmong.api.explore.dto;

import java.util.List;
import lombok.Data;

@Data
public class SolvedAcUserSolvedResponseDto {
    private int count;
    private List<ProblemItem> items;

    @Data
    public static class ProblemItem {
        private int problemId;
    }
}
