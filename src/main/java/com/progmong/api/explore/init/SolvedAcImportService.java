package com.progmong.api.explore.init;

import com.progmong.api.explore.entity.Problem;
import com.progmong.api.explore.repository.ProblemRepository;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Service
@RequiredArgsConstructor
public class SolvedAcImportService {

    private static final List<String> TAG_PRIORITY = List.of(
            "geometry", "dp", "graphs", "data_structures", "greedy", "string", "implementation", "math"
    );
    private static final Map<String, String> TAG_KO_MAP = Map.of(
            "geometry", "기하학",
            "dp", "DP",
            "graphs", "그래프",
            "data_structures", "자료구조",
            "greedy", "그리디",
            "string", "문자열",
            "implementation", "구현",
            "math", "수학"
    );
    private final ProblemRepository problemRepository;
    private final RestTemplate restTemplate = new RestTemplate();

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

        List<String> levelRanges = List.of("(*1..5)", "(*6..10)", "(*11..15)", "(*16..20)", "(*21..25)");
        for (String levelRange : levelRanges) {
            for (int page = 1; page <= 2; page++) {
                String tagQuery = "tag:math or tag:implementation or tag:greedy or tag:string or " +
                        "tag:data_structures or tag:graphs or tag:dp or tag:geometry";
                String query = levelRange + " " + tagQuery;

                String url = UriComponentsBuilder
                        .fromHttpUrl("https://solved.ac/api/v3/search/problem")
                        .queryParam("query", query)
                        .queryParam("sort", "solved")
                        .queryParam("direction", "desc")
                        .queryParam("page", page)
                        .toUriString();

                log.info("🔗 요청 URL: {}", url);

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

                                String mainTagKo = TAG_KO_MAP.getOrDefault(mainTag, "기타");
                                return new Problem(
                                        (long) item.getProblemId(),
                                        item.getTitleKo(),
                                        item.getLevel(),
                                        mainTag,
                                        item.getAcceptedUserCount(),
                                        mainTagKo
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
        }

        log.info("✅ 총 저장된 문제 수: {}", totalSaved);
    }
}
