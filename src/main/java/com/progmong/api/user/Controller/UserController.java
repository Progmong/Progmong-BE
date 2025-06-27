package com.progmong.api.user.Controller;

import com.progmong.api.user.dto.*;
import com.progmong.api.user.service.EmailService;
import com.progmong.api.user.service.UserService;
import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.exception.BadRequestException;
import com.progmong.common.exception.UnauthorizedException;
import com.progmong.common.response.ApiResponse;
import com.progmong.common.response.ErrorStatus;
import com.progmong.common.response.SuccessStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.validator.routines.EmailValidator;


import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@RestController
@Tag(name = "user", description = "사용자 관련 API")
public class UserController {

    private final UserService userService;
    private final EmailService emailService;

    @Operation(
            summary = "회원가입 API"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> registerUser(@RequestBody UserRegisterRequestDto userRegisterRequestDto) {
        userService.registerUser(userRegisterRequestDto);
        return ApiResponse.success_only(SuccessStatus.USER_REGISTERED);
    }
    @Operation(
            summary = "이메일 인증코드 발송 API",
            description = "이메일 인증 코드를 발송합니다.<br>"
            +"<p>"
            +"호출 필드 정보) <br>"
            +"email : 사용자 이메일"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 인증코드 발송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "올바른 이메일 형식이 아닙니다."),
    })
    @PostMapping("/verify-email")
    public ResponseEntity<ApiResponse<Void>> getEmailVerification(@RequestBody EmailVerificationRequestDto emailVerificationRequestDto) {
        LocalDateTime requestedAt = LocalDateTime.now();
        String email = emailVerificationRequestDto.getEmail();

        // Apache Commons EmailValidator 검증
        if (!EmailValidator.getInstance().isValid(email)) {
            throw new BadRequestException(ErrorStatus.VALIDATION_EMAIL_FORMAT_EXCEPTION.getMessage());
        }

        emailService.sendVerificationEmail(email, requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_CODE_SUCCESS);
    }
    @Operation(
            summary = "이메일 코드 인증 API",
            description = "발송된 이메일 인증 코드를 검증합니다.<br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : 이메일로 발송된 인증코드"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "이메일 코드 인증 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "이메일 인증코드가 올바르지 않습니다."),
    })
    @PostMapping("/verification-email-code")
    public ResponseEntity<ApiResponse<Void>> verificationByCode(@RequestBody EmailVerificationCodeRequestDto emailVerificationCodeRequestDto) {
        LocalDateTime requestedAt = LocalDateTime.now();
        emailService.verifyEmail(emailVerificationCodeRequestDto.getCode(), requestedAt);
        return ApiResponse.success_only(SuccessStatus.SEND_EMAIL_VERIFICATION_SUCCESS);
    }


    @Operation(
            summary = "로그인 API"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "로그인 정보가 유효하지 않습니다"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<UserLoginResponseDto>> login(@RequestBody UserLoginRequestDto userLoginRequestDto) {
        UserLoginResponseDto responseDto = userService.login(userLoginRequestDto);
        return ApiResponse.success(SuccessStatus.USER_LOGGED_IN, responseDto);
    }
    @Operation(
            summary = "비밀번호 초기화 요청 API",
            description = "이메일로 비밀번호 초기화 코드를 보냅니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 초기화 코드 전송 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "잘못된 요청입니다."),
    })
    @PostMapping("/reset-password/request")
    public ResponseEntity<ApiResponse<Void>> requestPasswordReset(@RequestBody PasswordResetRequestDto passwordResetRequestDto) {
        emailService.sendPasswordResetEmail(passwordResetRequestDto);
        return ApiResponse.success_only(SuccessStatus.SEND_PASSWORD_RESET_CODE_SUCCESS);
    }
    @Operation(
            summary = "비밀번호 초기화 API",
            description = "이메일에서 받은 코드를 이용해 비밀번호를 변경합니다. <br>"
                    + "<p>"
                    + "호출 필드 정보) <br>"
                    + "code : 비밀번호 초기화 인증코드 <br>"
                    + "newPassword : 새 비밀번호"
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "비밀번호 변경 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "유효하지 않은 비밀번호 초기화 인증코드 입니다."),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "비밀번호 초기화 인증코드가 만료되었습니다, 재인증 해주세요."),
    })
    @PostMapping("/reset-password/confirm")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@RequestBody PasswordResetConfirmDto passwordResetConfirmDto) {

        userService.resetPassword(passwordResetConfirmDto);
        return ApiResponse.success_only(SuccessStatus.SEND_UPDATE_USER_PASSWORD);
    }

    @Operation(
            summary = "사용자 정보 조회 API",
            description = "토큰을 통해 인증된 사용자의 정보를 반환합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "사용자 정보 조회 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "해당 유저를 찾을 수 없습니다.")
    })
    @GetMapping("/user-info")
    public ResponseEntity<ApiResponse<UserInfoResponseDto>> getUserInfo(@AuthenticationPrincipal SecurityUser securityUser) {

        UserInfoResponseDto userInfo = userService.getUserInfo(securityUser.getId());
        return ApiResponse.success(SuccessStatus.GET_USER_INFO_SUCCESS, userInfo);
    }

    @PostMapping("/reissue")
    @Operation(
            summary = "Access Token 재발급 API",
            description = "Refresh Token을 이용해 Access Token을 재발급합니다."
    )
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Access Token 재발급 성공"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Refresh Token이 유효하지 않습니다.")
    })
    public ResponseEntity<ApiResponse<UserAccessTokenResponseDto>> reissueAccessToken(
            HttpServletRequest request) {

        String refreshTokenHeader = request.getHeader("Authorization_refresh");
        if (refreshTokenHeader == null ) {
            throw new UnauthorizedException("Refresh Token이 없습니다.");
        }

        UserAccessTokenResponseDto responseDto = userService.reissueAccessTokenByRefreshToken(refreshTokenHeader);
        return ApiResponse.success(SuccessStatus.OK, responseDto);
    }


}