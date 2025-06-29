package com.progmong.api.tag.controller;

import com.progmong.api.tag.dto.InterestTagResponseDto;
import com.progmong.api.tag.dto.InterestTagUpdateRequestDto;
import com.progmong.api.tag.service.InterestTagService;
import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.response.ApiResponse;
import com.progmong.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "InterestTag", description = "관심 태그 관련 API입니다.")
@RestController
@RequestMapping("/api/v1/tag")
@RequiredArgsConstructor
public class InterestTagController {

    private final InterestTagService interestTagService;

    @PutMapping
    @Operation(summary = "관심 태그 수정", description = "사용자가 선택한 태그 목록을 기반으로 관심 태그를 갱신합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "관심 태그 수정 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "유저 또는 태그가 존재하지 않음"),
    })
    public ResponseEntity<ApiResponse<Void>> updateInterestTags(
            @RequestBody InterestTagUpdateRequestDto request,
            @AuthenticationPrincipal SecurityUser user) {
        interestTagService.updateInterestTags(user.getId(), request.getTagIds());
        return ApiResponse.success_only(SuccessStatus.INTEREST_TAG_UPDATED);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<InterestTagResponseDto>>> getUserInterestTags(
            @AuthenticationPrincipal SecurityUser user) {
        List<InterestTagResponseDto> tags = interestTagService.getUserInterestTags(user.getId());
        return ApiResponse.success(SuccessStatus.INTEREST_TAG_FOUND, tags);
    }
}
