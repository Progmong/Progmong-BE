package com.progmong.api.user.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.exception.UnauthorizedException;
import java.util.Date;
import java.util.Optional;

import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Getter
@Slf4j
public class JwtService {

    private final UserRepository userRepository;
    @Value("${jwt.secretKey}")
    private String secretKey;

    @Value("${jwt.refresh.secretKey}")
    private String refreshSecretKey;

    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    @Value("${jwt.refresh.expiration}")
    private Long refreshTokenExpirationPeriod;

    // Access Token 생성
    public String createAccessToken(Long userId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    public String createRefreshToken(Long userId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(refreshSecretKey));
    }

    // Access Token 유효성 검사
    public boolean isTokenValid(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("토큰이 제공되지 않았습니다.");
        }

        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new UnauthorizedException("토큰이 만료되었습니다.");
        } catch (SignatureVerificationException e) {
            throw new UnauthorizedException("토큰 서명 검증에 실패했습니다.");
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 토큰입니다.");
        }
    }

    // Refresh Token 유효성 검사
    public boolean isRefreshTokenValid(String token) {
        if (token == null || token.isBlank()) {
            throw new UnauthorizedException("Refresh Token이 제공되지 않았습니다.");
        }

        try {
            JWT.require(Algorithm.HMAC512(refreshSecretKey)).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            throw new UnauthorizedException("Refresh Token이 만료되었습니다.");
        } catch (SignatureVerificationException e) {
            throw new UnauthorizedException("Refresh Token 서명 검증에 실패했습니다.");
        } catch (Exception e) {
            throw new UnauthorizedException("유효하지 않은 Refresh Token입니다.");
        }
    }


    // 토큰에서 회원 ID 추출
    public Optional<String> extractUserId(String accessToken) {
        try {
            String sub = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim("sub")
                    .asString();
            return Optional.ofNullable(sub);
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    public Optional<String> extractUserIdByRefresh(String refreshToken) {
        try {
            String sub = JWT.require(Algorithm.HMAC512(refreshSecretKey))
                    .build()
                    .verify(refreshToken)
                    .getClaim("sub")
                    .asString();
            return Optional.ofNullable(sub);
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    // (Optional) 이메일 추출 메서드. 필요 없으면 삭제해도 됨
    public Optional<String> extractEmail(String accessToken) {
        try {
            String sub = JWT.require(Algorithm.HMAC512(secretKey))
                    .build()
                    .verify(accessToken)
                    .getClaim("sub")
                    .asString();
            return Optional.ofNullable(sub);
        } catch (Exception e) {
            log.error("액세스 토큰이 유효하지 않습니다.");
            return Optional.empty();
        }
    }

    public void updateRefreshToken(Long userId, String newRefreshToken) {
        userRepository.findById(userId).ifPresent(user -> {
            user.updateRefreshToken(newRefreshToken); // User 엔티티에 이 메서드 있어야 함
            userRepository.save(user);
        });
    }

    public Cookie createRefreshTokenCookie(String refreshToken) {
        Cookie refreshCookie = new Cookie("Authorization_refresh", refreshToken);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(false); // 개발환경일 때 false
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7); // 7일
        // refreshCookie.setDomain("localhost");  // 개발환경에서는 생략 권장
        refreshCookie.setAttribute("SameSite", "Lax"); // 혹은 "None" + Secure:true 환경에서
        return refreshCookie;
    }


    public void removeRefreshToken(Long userId) {
        userRepository.findById(userId)
                .ifPresent(user -> {
                    user.removeRefreshToken();
                    userRepository.save(user);
                });
    }



}
