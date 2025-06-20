package com.progmong.api.explore.init;

import lombok.Data;

import java.util.List;

@Data
public class SolvedAcProblemResponse {
    private int count;
    private List<ProblemItem> items;

    @Data
    public static class ProblemItem {
        private int problemId;
        private int acceptedUserCount;
        private String titleKo;
        private int level;
        private List<Tag> tags;

        @Data
        public static class Tag {
            private String key;
        }
    }
}