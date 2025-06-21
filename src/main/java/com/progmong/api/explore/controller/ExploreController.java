package com.progmong.api.explore.controller;

import com.progmong.api.explore.dto.ExploreStartRequestDto;
import com.progmong.api.explore.dto.ExploreStartResponseDto;
import com.progmong.api.explore.service.ExploreService;
import com.progmong.common.response.ApiResponse;
import com.progmong.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/explore")
@Tag(name = "Explore", description = "탐험 관련 API")
@RequiredArgsConstructor
public class ExploreController {

    private final ExploreService exploreService;

    @PostMapping("/start")
    @Operation(
            summary = "탐험 시작",
            description = "유저의 펫 상태를 전투로 변경하고 문제 5개를 추천합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탐험 시작 성공")
    })
    public ResponseEntity<ApiResponse<ExploreStartResponseDto>> startExplore(
            Long userId,    // jwt 구현 시 변경
            @RequestBody ExploreStartRequestDto requestDto
    ) {
        ExploreStartResponseDto exploreStartResponseDto = exploreService.startExplore(userId,requestDto.getMinLevel(), requestDto.getMaxLevel());
        return ApiResponse.success(SuccessStatus.EXPLORE_START,exploreStartResponseDto);
    }
}