package com.progmong.api.user.service;

import com.progmong.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class SolvedacService {

    private final RestTemplateBuilder builder;
    private final UserRepository userRepository;

    private final Map<String, String> verificationCodes = new ConcurrentHashMap<>();

    public ResponseEntity<?> getUserFromSolvedac(String handle) {
        RestTemplate restTemplate = builder.build();
        String url = "https://solved.ac/api/v3/user/show?handle=" + handle;
        try {
            return restTemplate.getForEntity(url, String.class);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Solved.ac 요청 실패: " + e.getMessage());
        }
    }

    public ResponseEntity<?> generateVerificationCode(String handle) {
        if (userRepository.findByBojId(handle).isPresent()) {
            return ResponseEntity.badRequest().body("이미 인증된 사용자입니다.");
        }

        String code = String.valueOf(new Random().nextInt(900000) + 100000);
        verificationCodes.put(handle, code);

        return ResponseEntity.ok(Map.of(
                "handle", handle,
                "verificationCode", code,
                "message", "solved.ac bio에 이 코드를 입력한 뒤 인증 확인을 눌러주세요."
        ));
    }

    public ResponseEntity<?> verifyCodeFromBio(String handle) {
        String expectedCode = verificationCodes.get(handle);
        if (expectedCode == null) {
            return ResponseEntity.badRequest().body("해당 사용자에 대한 인증 코드가 없습니다.");
        }

        RestTemplate restTemplate = builder.build();
        String url = "https://solved.ac/api/v3/user/show?handle=" + handle;

        try {
            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            String bio = (String) response.getBody().get("bio");

            if (bio != null && bio.contains(expectedCode)) {
                verificationCodes.remove(handle);
                return ResponseEntity.ok(Map.of(
                        "handle", handle,
                        "verified", true,
                        "message", "인증 성공!"
                ));
            } else {
                return ResponseEntity.status(401).body("bio에 올바른 인증 코드가 없습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Solved.ac 요청 실패: " + e.getMessage());
        }
    }
}
