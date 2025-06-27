package com.progmong.api.user.jwt.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.SignatureVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.progmong.api.user.repository.UserRepository;
import java.util.Date;
import java.util.Optional;
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
    @Value("${jwt.access.expiration}")
    private Long accessTokenExpirationPeriod;

    // Access Token 생성
    public String createAccessToken(Long userId) {
        Date now = new Date();
        return JWT.create()
                .withSubject(String.valueOf(userId))
                .withExpiresAt(new Date(now.getTime() + accessTokenExpirationPeriod))
                .sign(Algorithm.HMAC512(secretKey));
    }

    // 토큰 유효성 검사
    public boolean isTokenValid(String token) {
        try {
            JWT.require(Algorithm.HMAC512(secretKey)).build().verify(token);
            return true;
        } catch (TokenExpiredException e) {
            log.error("토큰이 만료되었습니다: {}", e.getMessage());
            return false;
        } catch (SignatureVerificationException e) {
            log.error("토큰 서명 검증 실패: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.error("유효하지 않은 토큰입니다. {}", e.getMessage());
            return false;
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
}
