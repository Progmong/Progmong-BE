package com.progmong.api.pet.controller;

import com.progmong.api.pet.dto.PetRegisterRequestDto;
import com.progmong.api.pet.dto.UserPetFullDto;
import com.progmong.api.pet.service.UserPetService;
import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.response.ApiResponse;
import com.progmong.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Pet", description = "펫 관련 API입니다.")
@RestController
@RequestMapping("/api/v1/pet")
@RequiredArgsConstructor
public class UserPetController {
    private final UserPetService userPetService;

    @PostMapping("/register")
    @Operation(summary = "펫 등록", description = "사용자가 펫의 종류와 닉네임을 작성하여 등록합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "펫 등록 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 등록된 펫이 있는 경우, 닉네임이 중복된 경우"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "존재하지 않는 유저인 경우 또는 존재하지 않는 펫을 선택한 경우"),
    })

    public ResponseEntity<ApiResponse<Void>> registerPet(
            @RequestBody PetRegisterRequestDto request,
            @AuthenticationPrincipal SecurityUser user) {

        Long userId = user.getId();
        userPetService.registerPet(request, userId);
        return ApiResponse.success_only(SuccessStatus.PET_REGISTERED);
    }

    @GetMapping("/all")
    @Operation(summary = "내 펫 정보 조회", description = "로그인한 사용자의 펫 전체 정보를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저의 펫 정보가 없음")
    })
    public ResponseEntity<ApiResponse<UserPetFullDto>> getUserPetFullInfo(
            @AuthenticationPrincipal SecurityUser user) {
        Long userId = user.getId();
        UserPetFullDto dto = userPetService.getUserPetFullInfo(userId);
        if (dto.getUserPetId() == null) {
            return ApiResponse.success(SuccessStatus.PET_INFO_NOT_FOUND,dto);
        }
        return ApiResponse.success(SuccessStatus.PET_INFO_LOADED, dto);
    }

    @PatchMapping("/nickname")
    @Operation(summary = "내 펫 닉네임 변경", description = "로그인한 사용자의 펫 닉네임을 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "닉네임 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이미 존재하는 닉네임인 경우"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저의 펫 정보가 없음")
    })
    public ResponseEntity<ApiResponse<Void>> updatePetNickname(
            @RequestBody String petNickname,
            @AuthenticationPrincipal SecurityUser user) {

        Long userId = user.getId();
        userPetService.updatePetNickname(petNickname, userId);
        return ApiResponse.success_only(SuccessStatus.PET_NICKNAME_UPDATED);
    }

    @PatchMapping("/message")
    @Operation(summary = "내 펫 메시지 변경", description = "로그인한 사용자의 펫 메시지를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "메시지 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저의 펫 정보가 없음")
    })
    public ResponseEntity<ApiResponse<Void>> updatePetMessage(
            @RequestBody String message,
            @AuthenticationPrincipal SecurityUser user) {

        Long userId = user.getId();
        userPetService.updatePetMessage(userId, message);
        return ApiResponse.success_only(SuccessStatus.PET_MESSAGE_UPDATED);
    }

    @GetMapping("/proud")
    @Operation(summary = "내 펫 공개 여부 조회", description = "로그인한 사용자의 펫 공개여부를 조회합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "펫 공개 여부 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저의 펫 정보가 없음")
    })
    public ResponseEntity<ApiResponse<Boolean>> isPetProud(
            @AuthenticationPrincipal SecurityUser user) {

        Long userId = user.getId();
        boolean isProud = userPetService.isPetProud(userId);
        return ApiResponse.success(SuccessStatus.PET_PROUD_STATUS_LOADED, isProud);
    }

    @PatchMapping("/proud")
    @Operation(summary = "내 펫 공개 여부 변경", description = "로그인한 사용자의 펫 공개 여부를 변경합니다.")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "펫 공개 여부 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저의 펫 정보가 없음")
    })
    public ResponseEntity<ApiResponse<Void>> togglePetVisibility(
            @AuthenticationPrincipal SecurityUser user) {
        Long userId = user.getId();
        userPetService.togglePetVisibility(userId);
        return ApiResponse.success_only(SuccessStatus.PET_STATUS_UPDATED);
    }
}
