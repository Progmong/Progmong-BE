package com.progmong.api.explore.dto;

import lombok.Data;

import java.util.List;

@Data
public class SolvedAcUserSolvedResponseDto {
    private int count;
    private List<ProblemItem> items;

    @Data
    public static class ProblemItem {
        private int problemId;
    }
}