package com.progmong.api.explore.controller;

import com.progmong.api.explore.dto.ExploreStartRequestDto;
import com.progmong.api.explore.dto.ProblemRecordListQueryDto;
import com.progmong.api.explore.dto.RecommendProblemListResponseDto;
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

    @PostMapping
    @Operation(
            summary = "탐험 시작",
            description = "유저의 펫 상태를 전투로 변경하고 문제 5개를 추천합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탐험 시작 성공")
    })
    public ResponseEntity<ApiResponse<RecommendProblemListResponseDto>> startExplore(
            Long userId,    // jwt 구현 시 변경
            @RequestBody ExploreStartRequestDto requestDto
    ) {
        RecommendProblemListResponseDto recommendProblemListDto = exploreService.startExplore(userId,requestDto.getMinLevel(), requestDto.getMaxLevel());
        return ApiResponse.success(SuccessStatus.EXPLORE_START,recommendProblemListDto);
    }

    @PostMapping("/pass")
    @Operation(
            summary = "탐험 패스",
            description = "현재 문제를 패스하고 다음 문제로 전환"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탐험 패스 성공")
    })
    public ResponseEntity<ApiResponse<RecommendProblemListResponseDto>> passExplore(
            Long userId   // jwt 구현 시 변경
    ) {
        RecommendProblemListResponseDto recommendProblemListDto = exploreService.passExplore(userId);
        return ApiResponse.success(SuccessStatus.EXPLORE_START,recommendProblemListDto);
    }

    @GetMapping
    @Operation(
            summary = "현재 탐험 조회",
            description = "현재 진행 중인 탐험의 문제들을 조회"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탐험 조회 성공")
    })
    public ResponseEntity<ApiResponse<RecommendProblemListResponseDto>> currentExplore(
            Long userId   // jwt 구현 시 변경
    ) {
        RecommendProblemListResponseDto recommendProblemListDto = exploreService.currentExplore(userId);
        return ApiResponse.success(SuccessStatus.EXPLORE_START,recommendProblemListDto);
    }


    @GetMapping("/records/all")
    public ApiResponse<ProblemRecordListQueryDto> getAllRecords(Long userId) {
        return ApiResponse.success(SuccessStatus.GET_ALL_RECORD,exploreService.getAllProblemRecords(userId)).getBody();
    }

    @GetMapping("/records")
    public ApiResponse<ProblemRecordListQueryDto> getRecentRecords(
            Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        return ApiResponse.success(SuccessStatus.GET_PAGED_RECORD,exploreService.getRecentProblemRecords(userId, page, size)).getBody();
    }

    @GetMapping("/records/count")
    public ApiResponse<Long> getTotalRecordCount(Long userId) {
        return ApiResponse.success(SuccessStatus.GET_ALL_RECORD_COUNT,exploreService.getProblemRecordCount(userId)).getBody();
    }

    @PostMapping("/success")
    @Operation(
            summary = "탐험 문제 풀이 성공",
            description = "현재 전투 중인 문제를 성공 처리하고 경험치를 반영합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "탐험 문제 풀이 성공 완료")
    })
    public ResponseEntity<ApiResponse<RecommendProblemListResponseDto>> successExplore(
            @RequestParam Long userId
    ) {
        RecommendProblemListResponseDto result = exploreService.successExplore(userId);
        return ApiResponse.success(SuccessStatus.EXPLORE_PROBLEM_SUCCESS, result);
    }

}