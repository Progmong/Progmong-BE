package com.progmong.api.explore.service;


import com.progmong.api.explore.dto.SolvedAcUserSolvedResponseDto;
import com.progmong.api.explore.entity.Problem;
import com.progmong.api.explore.entity.SolvedProblem;
import com.progmong.api.explore.repository.ProblemRepository;
import com.progmong.api.explore.repository.SolvedProblemRepository;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolvedProblemSyncService {
    private final RestTemplate restTemplate = new RestTemplate();
    private final SolvedProblemRepository solvedProblemRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    public void syncSolvedProblems(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        String bojId = user.getBojId();
        int page = 1;
        int totalSaved = 0;

        while (true) {
            String url = UriComponentsBuilder
                    .fromHttpUrl("https://solved.ac/api/v3/search/problem")
                    .queryParam("query", "solved_by:" + bojId)
                    .queryParam("page", page)
                    .toUriString();

            try {
                ResponseEntity<SolvedAcUserSolvedResponseDto> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        getDefaultHeader(),
                        SolvedAcUserSolvedResponseDto.class
                );

                SolvedAcUserSolvedResponseDto body = response.getBody();
                if (body == null || body.getItems().isEmpty()) break;

                List<Long> problemIds = body.getItems().stream()
                        .map(item -> (long) item.getProblemId())
                        .toList();

                Map<Long, Problem> problemMap = problemRepository.findAllById(problemIds).stream()
                        .collect(Collectors.toMap(Problem::getId, Function.identity()));


                List<SolvedProblem> solvedList = body.getItems().stream()
                        .map(item -> {
                            Long problemId = (long) item.getProblemId();
                            Problem problem = problemMap.get(problemId);
                            if (problem == null) return null;

                            return SolvedProblem.builder()
                                    .user(user)
                                    .problem(problem)
                                    .build();
                        })
                        .filter(Objects::nonNull)
                        .toList();

                solvedProblemRepository.saveAll(solvedList);
                totalSaved += solvedList.size();
                log.info("{} 페이지 저장: {}개", page, solvedList.size());
                page++;

            } catch (Exception e) {
                log.error("❌ solved.ac 호출 실패: {}", e.getMessage());
                break;
            }
        }

        log.info("✅ 푼 문제 동기화 완료! 총 저장: {}개", totalSaved);
    }

    private HttpEntity<String> getDefaultHeader() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        return new HttpEntity<>(headers);
    }
}