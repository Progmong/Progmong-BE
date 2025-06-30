package com.progmong.api.user.service;

import com.progmong.api.explore.service.SolvedProblemSyncService;
import com.progmong.api.user.dto.*;
import com.progmong.api.user.entity.EmailVerification;
import com.progmong.api.user.entity.PasswordReset;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.jwt.service.JwtService;
import com.progmong.api.user.repository.EmailVerificationRepository;
import com.progmong.api.user.repository.PasswordResetRepository;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.BadRequestException;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.exception.UnauthorizedException;
import com.progmong.common.response.ErrorStatus;
import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordEncoder passwordEncoder;
    private final PasswordResetRepository passwordResetRepository;
    private final JwtService jwtService;
    private final SolvedProblemSyncService solvedProblemSyncService;


    public void registerUser(UserRegisterRequestDto userRegisterRequestDto) {
        // 이메일 중복 검증
        if (userRepository.findByEmail(userRegisterRequestDto.getEmail()).isPresent()) {
            throw new BadRequestException(ErrorStatus.DUPLICATE_EMAIL.getMessage());
        }
        // 이메일 인증 여부 체크
        EmailVerification emailVerification = emailVerificationRepository.findByEmail(userRegisterRequestDto.getEmail())
                .orElseThrow(
                        () -> new BadRequestException((ErrorStatus.MISSING_EMAIL_VERIFICATION_EXCEPTION.getMessage())));

        // 닉네임 중복 검증
        if (userRepository.findByNickname(userRegisterRequestDto.getNickname()).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_NICKNAME.getMessage());
        }
        // User 엔티티 생성
        User user = User.builder()
                .email(userRegisterRequestDto.getEmail())
                .password(passwordEncoder.encode(userRegisterRequestDto.getPassword()))
                .nickname(userRegisterRequestDto.getNickname())
                .bojId(userRegisterRequestDto.getBojId())
                .build();

        userRepository.save(user);
        solvedProblemSyncService.syncSolvedProblems(user.getId());

    }

    // 로그인
    @Transactional(readOnly = true)
    public UserLoginResponseDto login(UserLoginRequestDto userLoginRequestDto) {

        // 이메일로 회원 검색
        User user = userRepository.findByEmail(userLoginRequestDto.getEmail())
                .orElseThrow(() -> new NotFoundException("해당 이메일을 찾을 수 없습니다."));

        // 비밀번호 검증
        if (!passwordEncoder.matches(userLoginRequestDto.getPassword(), user.getPassword())) {
            throw new BadRequestException("아이디 또는 비밀번호가 일치하지 않습니다.");
        }

        // JWT 토큰 생성 (Access)
        String accessToken = jwtService.createAccessToken(user.getId());
        // JWT 토큰 생성 (Refresh)
        String refreshToken = jwtService.createRefreshToken(user.getId());


        return new UserLoginResponseDto(accessToken, refreshToken);
    }

    // 비밀번호 초기화
    @Transactional
    public void resetPassword(PasswordResetConfirmDto passwordResetConfirmDto) {

        // 인증 번호 체크
        PasswordReset passwordReset = passwordResetRepository.findByCode(passwordResetConfirmDto.getCode())
                .orElseThrow(
                        () -> new BadRequestException(ErrorStatus.INVALID_PASSWORD_RESET_CODE_EXCEPTION.getMessage()));

        // 인증 코드 만료 체크
        if (LocalDateTime.now().isAfter(passwordReset.getExpirationTime())) {
            throw new UnauthorizedException(ErrorStatus.EXPIRED_PASSWORD_RESET_CODE_EXCEPTION.getMessage());
        }

        User user = userRepository.findByEmail(passwordReset.getEmail())
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        user.updatePassword(passwordEncoder.encode(passwordResetConfirmDto.getNewPassword()));
        userRepository.save(user);

        passwordResetRepository.delete(passwordReset);
    }

    // 사용자 정보 조회
    @Transactional(readOnly = true)
    public UserInfoResponseDto getUserInfo(Long id) {

        // 해당 유저를 찾을 수 없을 경우 예외처리
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        return new UserInfoResponseDto(user.getId(), user.getBojId(), user.getEmail(), user.getNickname());
    }

    // 유저 닉네임 수정
    @Transactional
    public void updateNickname(Long userId, String nickname) {
        // 해당 유저를 찾을 수 없을 경우 예외처리
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        // 닉네임 중복 검증
        if (userRepository.findByNickname(nickname).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTERED_NICKNAME.getMessage());
        }

        user.updateNickname(nickname);
        userRepository.save(user);
    }

    // Access Token 재발급
    public UserAccessTokenResponseDto reissueAccessTokenByRefreshToken(String refreshToken) {
        if (!jwtService.isRefreshTokenValid(refreshToken)) {
            throw new UnauthorizedException("Refresh Token이 유효하지 않습니다.");
        }

        String userId = jwtService.extractUserIdByRefresh(refreshToken)
                .orElseThrow(() -> new UnauthorizedException("토큰에서 사용자 정보를 추출할 수 없습니다."));

        User user = userRepository.findById(Long.parseLong(userId))
                .orElseThrow(() -> new NotFoundException("해당 사용자를 찾을 수 없습니다."));

        String newAccessToken = jwtService.createAccessToken(user.getId());

        return new UserAccessTokenResponseDto(newAccessToken);
    }

    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        userRepository.delete(user);
    }


    public User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));
    }

    public UserInfoResponseDto toUserInfoResponseDto(User user) {
        return new UserInfoResponseDto(
                user.getId(),
                user.getBojId(),
                user.getEmail(),
                user.getNickname()
        );
    }
}
