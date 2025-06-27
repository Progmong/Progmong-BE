package com.progmong.api.user.jwt.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmong.api.user.entity.User;
import com.progmong.api.user.jwt.service.JwtService;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.config.security.SecurityUser;
import com.progmong.common.exception.UnauthorizedException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    @Value("${jwt.access.header}")
    private String accessTokenHeader;

    @Value("${jwt.refresh.header}")
    private String refreshTokenHeader;

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper; // ObjectMapper 주입 필요

    private static final String[] SWAGGER_URIS = {
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui/index.html"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        for (String uri : SWAGGER_URIS) {
            if (requestURI.startsWith(uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            // 1. Access Token 검증
            Optional<String> accessToken = extractToken(request, accessTokenHeader);

            if (accessToken.isPresent()) {
                boolean valid = jwtService.isTokenValid(accessToken.get()); // 여기서 exception 발생 가능
                if (valid) {
                    jwtService.extractUserId(accessToken.get())
                            .flatMap(id -> findAndAuthenticateUser(id, "AccessToken"))
                            .ifPresentOrElse(
                                    this::setAuthentication,
                                    () -> log.warn("AccessToken 유효하지만 사용자 정보 없음")
                            );
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            // 2. Refresh Token 검증
            Optional<String> refreshToken = extractRefreshToken(request, refreshTokenHeader);
            if (refreshToken.isPresent()) {
                boolean valid = jwtService.isRefreshTokenValid(refreshToken.get()); // exception 발생 가능
                if (valid) {
                    jwtService.extractUserIdByRefresh(refreshToken.get())
                            .flatMap(id -> findAndAuthenticateUser(id, "RefreshToken"))
                            .ifPresentOrElse(
                                    this::setAuthentication,
                                    () -> log.warn("RefreshToken 유효하지만 사용자 정보 없음")
                            );
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            // 둘 다 없거나 유효하지 않음 → 다음 필터로 그냥 넘김
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

    private Optional<String> extractToken(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    private Optional<String> extractRefreshToken(HttpServletRequest request, String headerName) {
        String refreshToken = request.getHeader(headerName);
        if (refreshToken != null) {
            return Optional.of(refreshToken);
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
