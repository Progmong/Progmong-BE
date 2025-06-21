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
    private static final Set<String> ALLOWED_TAGS = Set.of(
            "math", "implementation", "greedy", "string",
            "data_structures", "graphs", "dp", "geometry"
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

        for (int page = 1; page <= 5; page++) {
            String tagQuery = "tag:math or tag:implementation or tag:greedy or tag:string or " +
                    "tag:data_structures or tag:graphs or tag:dp or tag:geometry";

            String url = UriComponentsBuilder
                    .fromHttpUrl("https://solved.ac/api/v3/search/problem")
                    .queryParam("query", "s:5..15 " + tagQuery)
                    .queryParam("sort", "solved")
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
                                    .anyMatch(ALLOWED_TAGS::contains);
                        })
                        .map(item -> {
                            String mainTag = item.getTags().stream()
                                    .map(SolvedAcProblemResponseDto.ProblemItem.Tag::getKey) // Tag 클래스를 직접 명시
                                    .filter(ALLOWED_TAGS::contains)
                                    .findFirst()
                                    .orElseThrow(() -> new IllegalStateException("적절한 태그 없음"));
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

