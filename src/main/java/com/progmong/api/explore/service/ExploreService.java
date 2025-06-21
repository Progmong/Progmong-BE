package com.progmong.api.explore.service;


import com.progmong.api.explore.dto.ExploreStartResponseDto;
import com.progmong.api.explore.dto.RecommendProblemResponseDto;
import com.progmong.api.explore.entity.Problem;
import com.progmong.api.explore.entity.RecommendProblem;
import com.progmong.api.explore.entity.RecommendStatus;
import com.progmong.api.explore.repository.ProblemRepository;
import com.progmong.api.explore.repository.RecommendProblemRepository;
import com.progmong.api.pet.entity.PetStatus;
import com.progmong.api.pet.entity.UserPet;
import com.progmong.api.pet.repository.UserPetRepository;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ExploreService {
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final UserPetRepository userPetRepository;
    private final RecommendProblemRepository recommendProblemRepository;

    @Transactional
    public ExploreStartResponseDto startExplore(Long userId, int minLevel, int maxLevel) {
        // 1. 유저의 관심 태그 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        List<String> tags = user.getInterestTags().stream()
                .map(interestTag -> interestTag.getTag().getName())
                .toList();

        if (tags.isEmpty()) {
            throw new NotFoundException(ErrorStatus.USER_INTEREST_NOT_FOUND.getMessage());
        }


        // 2. 푼 문제 제외 랜덤 추천 (태그, 레벨 필터)
        List<Problem> recommend = problemRepository.findRandomUnsolvedProblemsByTagsAndLevel(
                tags, minLevel, maxLevel, userId
        );

        // 3. 첫 문제는 전투, 나머지는 대기로 상태 나눠서 저장
        List<RecommendProblem> recommendProblems = new ArrayList<>();
        for (int i = 0; i < recommend.size(); i++) {
            RecommendProblem rp = RecommendProblem.builder()
                    .user(user)
                    .problem(recommend.get(i))
                    .status(i == 0 ? RecommendStatus.전투 : RecommendStatus.대기)
                    .sequence(i + 1)
                    .build();
            recommendProblems.add(rp);
        }
        recommendProblemRepository.saveAll(recommendProblems);

        // 3. 펫 상태 전투로 변경
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_PET_NOT_FOUND.getMessage()));
        userPet.updateStatus(PetStatus.전투);

        // 4. 응답 DTO로 변환
        List<RecommendProblemResponseDto> recommendProblemResponseDto = recommendProblems.stream()
                .map(rp -> {
                    Problem p = rp.getProblem();
                    return new RecommendProblemResponseDto(
                            p.getId(),
                            p.getTitle(),
                            p.getLevel(),
                            p.getMainTag(),
                            p.getSolvedUserCount(),
                            rp.getStatus(),
                            rp.getSequence()
                    );
                })
                .toList();

        return new ExploreStartResponseDto(recommendProblemResponseDto);
    }
}
