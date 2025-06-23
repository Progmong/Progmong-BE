package com.progmong.api.explore.dto;

import com.progmong.api.explore.entity.Problem;
import com.progmong.api.explore.entity.RecommendProblem;
import com.progmong.api.explore.entity.RecommendStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecommendProblemResponseDto {
    private Long id;
    private String title;
    private int level;
    private String tier;
    private String mainTag;
    private int solvedUserCount;
    private RecommendStatus status;
    private int sequence;
    private int monsterImageIndex;

    public static RecommendProblemResponseDto fromEntity(RecommendProblem rp) {
        Problem p = rp.getProblem();
        return new RecommendProblemResponseDto(
                p.getId(),
                p.getTitle(),
                p.getLevel(),
                levelToTier(p.getLevel()),
                p.getMainTag(),
                p.getSolvedUserCount(),
                rp.getStatus(),
                rp.getSequence(),
                rp.getMonsterImageIndex()
        );
    }

    private static final String[] roman = {
            "", "I", "II", "III", "IV", "V"
    };

    private static String levelToTier(int level) {
        if (level >= 1 && level <= 5) return "브론즈 " + roman[6 - level];
        if (level >= 6 && level <= 10) return "실버 " + roman[11 - level];
        if (level >= 11 && level <= 15) return "골드 " + roman[16 - level];
        if (level >= 16 && level <= 20) return "플래티넘 " + roman[21 - level];
        if (level >= 21 && level <= 25) return "다이아 " + roman[26 - level];
        return "알 수 없음";
    }
}
