package com.progmong.api.user.Controller;

import com.progmong.api.user.service.SolvedacService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/solvedac")
@RequiredArgsConstructor
@Tag(name = "Solved.ac", description = "Solved.ac 관련 API")
public class SolvedacController {

    private final SolvedacService solvedacService;

    @Operation(
            summary = "Solved.ac 사용자 정보 조회 API",
            description = "Solved.ac 사용자 정보를 조회합니다."
    )
    @GetMapping("/{handle}")
    public ResponseEntity<?> getSolvedacUser(@PathVariable String handle) {
        return solvedacService.getUserFromSolvedac(handle);
    }

    @Operation(
            summary = "Solved.ac 코드 생성 API",
            description = "Solved.ac 코드를 생성합니다."
    )
    @GetMapping("/generate/{handle}")
    public ResponseEntity<?> generateVerificationCode(@PathVariable String handle) {
        return solvedacService.generateVerificationCode(handle);
    }


    @Operation(
            summary = "Solved.ac 코드 검증 API",
            description = "Solved.ac 바이오에 있는 코드를 검증합니다."
    )
    @GetMapping("/verify/{handle}")
    public ResponseEntity<?> verifyCodeFromBio(@PathVariable String handle) {
        return solvedacService.verifyCodeFromBio(handle);
    }
}
