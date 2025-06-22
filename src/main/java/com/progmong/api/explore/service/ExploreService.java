package com.progmong.api.explore.service;


import com.progmong.api.explore.dto.RecommendProblemListResponseDto;
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
    public RecommendProblemListResponseDto startExplore(Long userId, int minLevel, int maxLevel) {
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
                .map(RecommendProblemResponseDto::fromEntity)
                .toList();

        return new RecommendProblemListResponseDto(recommendProblemResponseDto);
    }

    @Transactional
    public RecommendProblemListResponseDto passExplore(Long userId) {
        // 1. 현재 전투 중인 문제를 패스로 변경
        RecommendProblem current = recommendProblemRepository.findByUserIdAndStatus(userId, RecommendStatus.전투)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.RECOMMEND_PROBLEM_IN_BATTLE_NOT_FOUND.getMessage()));
        current.updateStatus(RecommendStatus.패스);

        // 2. 다음 순서 문제를 전투로 변경
        int nextSequence = current.getSequence() + 1;
        RecommendProblem next = recommendProblemRepository
                .findByUserIdAndSequence(userId, nextSequence)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.NEXT_RECOMMEND_PROBLEM_NOT_FOUND.getMessage()));
        next.updateStatus(RecommendStatus.전투);

        // 3. 다시 추천 문제 전체 조회하여 반환
        List<RecommendProblem> recommendProblems = recommendProblemRepository.findAllByUserIdOrderBySequence(userId);
        List<RecommendProblemResponseDto> recommendProblemResponseDto = recommendProblems.stream()
                .map(RecommendProblemResponseDto::fromEntity)
                .toList();
        return new RecommendProblemListResponseDto(recommendProblemResponseDto);
    }
}
