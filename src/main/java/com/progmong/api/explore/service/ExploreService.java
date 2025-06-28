package com.progmong.api.explore.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmong.api.explore.dto.ProblemRecordListQueryDto;
import com.progmong.api.explore.dto.ProblemRecordQueryDto;
import com.progmong.api.explore.dto.RecommendProblemListResponseDto;
import com.progmong.api.explore.dto.RecommendProblemResponseDto;
import com.progmong.api.explore.entity.Problem;
import com.progmong.api.explore.entity.ProblemRecord;
import com.progmong.api.explore.entity.RecommendProblem;
import com.progmong.api.explore.entity.RecommendStatus;
import com.progmong.api.explore.entity.Result;
import com.progmong.api.explore.entity.SolvedProblem;
import com.progmong.api.explore.repository.ProblemRecordRepository;
import com.progmong.api.explore.repository.ProblemRepository;
import com.progmong.api.explore.repository.RecommendProblemRepository;
import com.progmong.api.explore.repository.SolvedProblemRepository;
import com.progmong.api.pet.entity.PetStatus;
import com.progmong.api.pet.entity.UserPet;
import com.progmong.api.pet.repository.UserPetRepository;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.BaseException;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ExploreService {
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;
    private final UserPetRepository userPetRepository;
    private final RecommendProblemRepository recommendProblemRepository;
    private final ProblemRecordRepository problemRecordRepository;
    private final SolvedProblemRepository solvedProblemRepository;

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
        List<Integer> monsterIndices = IntStream.rangeClosed(1, 10)
                .boxed()
                .collect(Collectors.toList());

        Collections.shuffle(monsterIndices);
        for (int i = 0; i < recommend.size(); i++) {
            Problem p = recommend.get(i);
            RecommendProblem rp = RecommendProblem.builder()
                    .user(user)
                    .problem(p)
                    .status(i == 0 ? RecommendStatus.전투 : RecommendStatus.대기)
                    .sequence(i + 1)
                    .exp(p.getLevel() * 10)
                    .monsterImageIndex(monsterIndices.get(i))
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

        return new RecommendProblemListResponseDto(recommendProblemResponseDto, false, null);
    }

    @Transactional
    public RecommendProblemListResponseDto passExplore(Long userId) {
        // 1. 현재 전투 중인 문제를 패스로 변경
        RecommendProblem current = recommendProblemRepository.findByUserIdAndStatus(userId, RecommendStatus.전투)
                .orElseThrow(
                        () -> new NotFoundException(ErrorStatus.RECOMMEND_PROBLEM_IN_BATTLE_NOT_FOUND.getMessage()));
        current.updateStatus(RecommendStatus.패스);

        // 2. 문제 기록 저장 (이미 존재하면 생략)
        boolean exists = problemRecordRepository
                .findByUserIdAndProblemId(userId, current.getProblem().getId())
                .isPresent();

        if (!exists) {
            ProblemRecord problemRecord = ProblemRecord.builder()
                    .user(current.getUser())
                    .problem(current.getProblem())
                    .result(Result.실패)
                    .build();
            problemRecordRepository.save(problemRecord);
        }

        // 3. 다음 순서 문제를 전투로 변경
        int nextSequence = current.getSequence() + 1;
        Optional<RecommendProblem> next = recommendProblemRepository
                .findByUserIdAndSequence(userId, nextSequence);
        if (next.isPresent()) {
            next.get().updateStatus(RecommendStatus.전투);
        } else {

        }

        // 4. 다음 문제 없으면 결과, 있으면 다음 문제

        List<RecommendProblem> recommendProblems = recommendProblemRepository.findAllByUserIdOrderBySequence(userId);
        List<RecommendProblemResponseDto> recommendProblemResponseDto = recommendProblems.stream()
                .map(RecommendProblemResponseDto::fromEntity)
                .toList();

        boolean isFinish = next.isEmpty();
        if (isFinish) {
            int totalGainedExp = recommendProblems.stream()
                    .filter(rp -> rp.getStatus() == RecommendStatus.성공)
                    .mapToInt(RecommendProblem::getExp)
                    .sum();

            recommendProblemRepository.deleteAllByUserId(userId);

            return new RecommendProblemListResponseDto(recommendProblemResponseDto, isFinish, totalGainedExp);
        } else {
            return new RecommendProblemListResponseDto(recommendProblemResponseDto, isFinish, null);
        }


    }

    @Transactional(readOnly = true)
    public RecommendProblemListResponseDto currentExplore(Long userId) {
        List<RecommendProblem> problems = recommendProblemRepository.findAllByUserIdOrderBySequence(userId);

        List<RecommendProblemResponseDto> problemDtos = problems.stream()
                .map(RecommendProblemResponseDto::fromEntity)
                .toList();

        return new RecommendProblemListResponseDto(problemDtos, false, null);
    }

    @Transactional
    public RecommendProblemListResponseDto successExplore(Long userId) {
        // 1. 현재 전투 중인 문제를 성공으로 변경
        RecommendProblem current = recommendProblemRepository.findByUserIdAndStatus(userId, RecommendStatus.전투)
                .orElseThrow(
                        () -> new NotFoundException(ErrorStatus.RECOMMEND_PROBLEM_IN_BATTLE_NOT_FOUND.getMessage()));
        current.updateStatus(RecommendStatus.성공);

        // 2. 경험치 펫에 반영
        UserPet userPet = userPetRepository.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_PET_NOT_FOUND.getMessage()));
        userPet.addExp(current.getExp());

        // 3. 문제 기록 저장
        Optional<ProblemRecord> optionalProblemRecord =
                problemRecordRepository.findByUserIdAndProblemId(userId, current.getProblem().getId());

        if (optionalProblemRecord.isPresent()) {
            ProblemRecord problemRecord = optionalProblemRecord.get();
            // 이미 실패였다면 성공으로 업데이트
            if (problemRecord.getResult() == Result.실패) {
                problemRecord.updateResult(Result.성공);
            }
        } else {
            // 없으면 새로 기록
            ProblemRecord problemRecord = ProblemRecord.builder()
                    .user(current.getUser())
                    .problem(current.getProblem())
                    .result(Result.성공)
                    .build();
            problemRecordRepository.save(problemRecord);
        }

        // 4. SolvedProblem 중복 체크 후 저장
        boolean alreadySolved = solvedProblemRepository.existsByUserIdAndProblemId(userId,
                current.getProblem().getId());
        if (!alreadySolved) {
            SolvedProblem solvedProblem = SolvedProblem.builder()
                    .user(current.getUser())
                    .problem(current.getProblem())
                    .build();
            solvedProblemRepository.save(solvedProblem);
        }

        // 5. 다음 문제 전투로 전환
        int nextSequence = current.getSequence() + 1;
        Optional<RecommendProblem> next = recommendProblemRepository
                .findByUserIdAndSequence(userId, nextSequence);

        next.ifPresent(p -> p.updateStatus(RecommendStatus.전투));

        // 6. 다음 문제 없으면 결과, 있으면 다음 문제
        List<RecommendProblem> recommendProblems = recommendProblemRepository.findAllByUserIdOrderBySequence(userId);
        List<RecommendProblemResponseDto> recommendProblemResponseDto = recommendProblems.stream()
                .map(RecommendProblemResponseDto::fromEntity)
                .toList();

        boolean isFinish = next.isEmpty();
        if (isFinish) {
            int totalGainedExp = recommendProblems.stream()
                    .filter(rp -> rp.getStatus() == RecommendStatus.성공)
                    .mapToInt(RecommendProblem::getExp)
                    .sum();

            recommendProblemRepository.deleteAllByUserId(userId);

            return new RecommendProblemListResponseDto(recommendProblemResponseDto, isFinish, totalGainedExp);
        } else {
            return new RecommendProblemListResponseDto(recommendProblemResponseDto, isFinish, null);
        }


    }

    @Transactional(readOnly = true)
    public ProblemRecordListQueryDto getAllProblemRecords(Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        // 2. 전체 사냥 기록 조회
        List<ProblemRecord> records = problemRecordRepository.findByUser(user, null).getContent();

        // 3. DTO 변환
        List<ProblemRecordQueryDto> recordDtos = records.stream()
                .map(ProblemRecordQueryDto::fromEntity)
                .toList();

        // 4. totalCount 계산
        long totalCount = problemRecordRepository.countByUser(user);

        return ProblemRecordListQueryDto.of(recordDtos, totalCount);
    }

    @Transactional(readOnly = true)
    public ProblemRecordListQueryDto getRecentProblemRecords(Long userId, int page, int size) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        // 2. 페이지네이션 설정
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));

        // 3. 사냥 기록 조회
        Page<ProblemRecord> recordsPage = problemRecordRepository.findByUser(user, pageable);

        // 4. DTO 변환
        List<ProblemRecordQueryDto> recordDtos = recordsPage.getContent().stream()
                .map(ProblemRecordQueryDto::fromEntity)
                .toList();

        // 5. 총 개수 포함 응답 반환
        return ProblemRecordListQueryDto.of(recordDtos, recordsPage.getTotalElements());
    }

    @Transactional(readOnly = true)
    public long getProblemRecordCount(Long userId) {
        // 1. 사용자 조회
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        // 2. 해당 사용자의 총 사냥 기록 수 반환
        return problemRecordRepository.countByUser(user);
    }

    public boolean checkSolvedAcProblem(String bojId, int problemId) {
        String url = "https://solved.ac/api/v3/search/problem?query=solved_by:" + bojId + "+id:" + problemId;

        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 응답에서 count 값 추출
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(response.body());
            int count = jsonNode.get("count").asInt();

            return count == 1;
        } catch (Exception e) {
            throw new BaseException(HttpStatus.INTERNAL_SERVER_ERROR, "solved.ac API 호출 실패");
        }
    }


}
