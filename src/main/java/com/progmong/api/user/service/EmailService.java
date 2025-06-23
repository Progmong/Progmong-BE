package com.progmong.api.user.service;

import com.progmong.api.user.dto.PasswordResetRequestDto;
import com.progmong.api.user.entity.EmailVerification;
import com.progmong.api.user.entity.PasswordReset;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.repository.EmailVerificationRepository;
import com.progmong.api.user.repository.PasswordResetRepository;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.BadRequestException;
import com.progmong.common.exception.NotFoundException;
import com.progmong.common.exception.UnauthorizedException;
import com.progmong.common.response.ErrorStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${spring.mail.username}")
    private String serviceEmail;

    private final JavaMailSender mailSender;
    private final UserRepository userRepository;
    private final EmailVerificationRepository emailVerificationRepository;
    private final PasswordResetRepository passwordResetRepository;

    public void sendVerificationEmail(String email, LocalDateTime requestedAt) {

        // 이메일 중복 등록 검증
        if (userRepository.findByEmail(email).isPresent()) {
            throw new BadRequestException(ErrorStatus.ALREADY_REGISTER_EMAIL_EXCEPETION.getMessage());
        }

        // 기존에 있는 Email 삭제
        emailVerificationRepository.findByEmail(email)
                .ifPresent(emailVerificationRepository::delete);

        String code = generateSixDigitCode();
        EmailVerification verification = EmailVerification.builder()
                .email(email)
                .code(code)
                .expirationTimeInMinutes(5)
                .isVerified(false)
                .build();
        emailVerificationRepository.save(verification);



        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(String.format("프로그몽 <%s>", serviceEmail));
        mailMessage.setTo(email);
        mailMessage.setSubject("프로그몽 회원가입 인증코드 입니다.");
        mailMessage.setText(verification.generateCodeMessage());
        mailSender.send(mailMessage);
    }

    public void sendPasswordResetEmail(PasswordResetRequestDto passwordResetRequestDto) {

        User user = userRepository.findByEmail(passwordResetRequestDto.getEmail())
                .orElseThrow(()->new NotFoundException(ErrorStatus.USER_NOT_FOUND.getMessage()));

        String resetCode = generateRandomCode();
        LocalDateTime expirationTime = LocalDateTime.now().plusMinutes(5);

        PasswordReset passwordReset = PasswordReset.builder()
                .email(passwordResetRequestDto.getEmail())
                .code(resetCode)
                .expirationTime(expirationTime)
                .build();

        passwordResetRepository.save(passwordReset);

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setFrom(String.format("프로그몽 <%s>", serviceEmail));
        mailMessage.setTo(passwordResetRequestDto.getEmail());
        mailMessage.setSubject("프로그몽 비밀번호 초기화 코드");
        mailMessage.setText("비밀번호를 재설정하려면 아래 코드를 입력하세요:\n\n"
        + "인증코드 : " + resetCode +"\n\n"
        + "이 코드는 5분 동안 유효합니다.");

        mailSender.send(mailMessage);
    }

    private String generateRandomCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder(6);
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        for (int i = 0; i < 6; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateSixDigitCode() {
        SecureRandom random = new SecureRandom();
        int number = random.nextInt(1000000); // 0 ~ 999999 범위
        return String.format("%06d", number); // 숫자 6자리
    }

    public void verifyEmail(String code, LocalDateTime requestedAt) {
        EmailVerification verification = emailVerificationRepository.findByCode(code)
                .orElseThrow(()-> new BadRequestException(ErrorStatus.WRONG_EMAIL_VERIFICATION_CODE_EXCEPTION.getMessage()));

        if (verification.isExpired(requestedAt)) {
            throw new UnauthorizedException(ErrorStatus.UNAUTHORIZED_EMAIL_VERIFICATION_CODE_EXCEPTION.getMessage());
        }

        verification.setIsVerified(true);
        emailVerificationRepository.save(verification);
    }
}
