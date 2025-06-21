package com.progmong.api.explore.init;

import com.progmong.api.explore.entity.Problem;
import com.progmong.api.explore.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class SolvedAcImportService {

    private final ProblemRepository problemRepository;
    private final RestTemplate restTemplate = new RestTemplate();
    private static final List<String> TAG_PRIORITY = List.of(
            "geometry", "dp", "graphs", "data_structures", "greedy", "string", "implementation", "math"
    );
    public boolean isProblemTableEmpty() {
        return problemRepository.count() == 0;
    }

    @Transactional
    public void importProblemsFromSolvedAc() {
        log.info("✅ solved.ac 문제 데이터 수집 시작!");

        HttpHeaders headers = new HttpHeaders();
        headers.set("User-Agent", "Mozilla/5.0");
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        HttpEntity<String> entity = new HttpEntity<>(headers);

        int totalSaved = 0;

        for (int page = 6; page <= 10; page++) {
            String tagQuery = "tag:math or tag:implementation or tag:greedy or tag:string or " +
                    "tag:data_structures or tag:graphs or tag:dp or tag:geometry";

            String url = UriComponentsBuilder
                    .fromHttpUrl("https://solved.ac/api/v3/search/problem")
                    .queryParam("query", "s:1..25 " + tagQuery)
                    .queryParam("sort", "random")
                    .queryParam("direction", "desc")
                    .queryParam("page", page)
                    .toUriString();

            try {
                ResponseEntity<SolvedAcProblemResponseDto> response = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        entity,
                        SolvedAcProblemResponseDto.class
                );

                SolvedAcProblemResponseDto body = response.getBody();
                if (body == null || body.getItems() == null) {
                    log.warn("❌ {}페이지 응답 없음", page);
                    continue;
                }

                List<Problem> problems = body.getItems().stream()
                        .filter(item -> {

                            return item.getTags().stream()
                                    .map(SolvedAcProblemResponseDto.ProblemItem.Tag::getKey)
                                    .anyMatch(TAG_PRIORITY::contains);
                        })
                        .map(item -> {
                            String mainTag = TAG_PRIORITY.stream()
                                    .filter(priorityTag -> item.getTags().stream()
                                            .map(SolvedAcProblemResponseDto.ProblemItem.Tag::getKey)
                                            .anyMatch(priorityTag::equals))
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("적절한 대표 태그 없음"));
                            return new Problem(
                                    (long) item.getProblemId(),
                                    item.getTitleKo(),
                                    item.getLevel(),
                                    mainTag,
                                    item.getAcceptedUserCount()
                            );
                        })
                        .toList();

                problemRepository.saveAll(problems);
                totalSaved += problems.size();
                log.info("📄 {}페이지 저장: {}개", page, problems.size());

            } catch (Exception e) {
                log.error("❌ {}페이지 실패: {}", page, e.getMessage());
            }
        }

        log.info("✅ 총 저장된 문제 수: {}", totalSaved);
    }
}

