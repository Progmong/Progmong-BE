package com.progmong.api.user.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.jwt.service.JwtService;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Optional;
import java.util.HashMap;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    @Value("${jwt.access.header}")
    private String accessTokenHeader;

    @Value("${jwt.refresh.header}")
    private String refreshTokenHeader; // 쿠키 이름

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    private static final String[] SWAGGER_URIS = {
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui/index.html"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();

        // Swagger 관련 요청 제외
        for (String uri : SWAGGER_URIS) {
            if (requestURI.startsWith(uri)) {
                return true;
            }
        }

        // 인증이 필요 없는 URI (permitAll 대상)
        return requestURI.startsWith("/api/v1/users/login");
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Access Token 검증
            Optional<String> accessToken = extractAccessToken(request, accessTokenHeader);
            if (accessToken.isPresent() && jwtService.isTokenValid(accessToken.get())) {
                jwtService.extractUserId(accessToken.get())
                        .flatMap(id -> findAndAuthenticateUser(id, "AccessToken"))
                        .ifPresentOrElse(
                                this::setAuthentication,
                                () -> log.warn("AccessToken 유효하지만 사용자 정보 없음")
                        );
                filterChain.doFilter(request, response);
                return;
            }

            // 2. Refresh Token 검증 (쿠키에서 추출)
            Optional<String> refreshToken = extractRefreshTokenFromCookie(request, refreshTokenHeader);
            if (refreshToken.isPresent() && jwtService.isRefreshTokenValid(refreshToken.get())) {
                jwtService.extractUserIdByRefresh(refreshToken.get())
                        .flatMap(id -> findAndAuthenticateUser(id, "Authorization_refresh"))
                        .ifPresentOrElse(
                                this::setAuthentication,
                                () -> log.warn("RefreshToken 유효하지만 사용자 정보 없음")
                        );
                filterChain.doFilter(request, response);
                return;
            }

            // 둘 다 없음 → 그냥 다음 필터로
            filterChain.doFilter(request, response);

        } catch (UnauthorizedException e) {
            sendErrorResponse(response, "UNAUTHORIZED", e.getMessage());
        } catch (Exception e) {
            sendErrorResponse(response, "UNKNOWN_ERROR", "알 수 없는 오류가 발생했습니다.");
        }
    }

    private Optional<User> findAndAuthenticateUser(String id, String tokenType) {
        try {
            Optional<User> user = userRepository.findById(Long.valueOf(id));
            user.ifPresent(u -> log.debug("{} 인증 성공: {}", tokenType, u.getEmail()));
            return user;
        } catch (NumberFormatException e) {
            log.error("{}로부터 잘못된 사용자 ID: {}", tokenType, id);
            return Optional.empty();
        }
    }

    private Optional<String> extractAccessToken(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    public static Optional<String> extractRefreshTokenFromCookie(HttpServletRequest request, String cookieName) {
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if (cookie.getName().equals(cookieName)) {
                    return Optional.of(cookie.getValue());
                }
            }
        }
        return Optional.empty();
    }

    private void setAuthentication(User user) {
        SecurityUser securityUser = SecurityUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .bojId(user.getBojId())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                securityUser,
                null,
                null
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendErrorResponse(HttpServletResponse response, String errorCode, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("error", errorCode);
        errorResponse.put("message", message);
        errorResponse.put("status", 401);
        errorResponse.put("timestamp", System.currentTimeMillis());

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
