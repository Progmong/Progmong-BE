package com.progmong.api.user.jwt.filter;

import com.progmong.api.user.entity.User;
import com.progmong.api.user.jwt.service.JwtService;
import com.progmong.api.user.repository.UserRepository;
import com.progmong.common.config.security.SecurityUser;
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
import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationProcessingFilter extends OncePerRequestFilter {

    @Value("${jwt.access.header}")
    private String accessTokenHeader;

    private final JwtService jwtService;
    private final UserRepository userRepository;

    // Swagger UI 등의 특정 URI를 필터 적용 대상에서 제외할 때 사용
    private static final String[] SWAGGER_URIS = {
            "/swagger-ui",
            "/v3/api-docs",
            "/swagger-ui/index.html"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String requestURI = request.getRequestURI();
        for(String uri : SWAGGER_URIS) {
            if(requestURI.startsWith(uri)) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        //요청 헤더에서 Access Token을 추출하고, 유효하다면 해당 회원 정보를 SecurityContext에 설정
        Optional<String> accessToken = extractToken(request, accessTokenHeader)
                .filter(jwtService::isTokenValid);

        accessToken.ifPresent(token ->
                jwtService.extractUserId(token)
                        .ifPresent(id->
                                userRepository.findById(Long.valueOf(id))
                                        .ifPresent(this::setAuthentication)
                        )
        );

        filterChain.doFilter(request, response);
    }

    // 요청 헤더에서 "Bearer " 로 시작하는 토큰 부분만 잘라서 반환
    private Optional<String> extractToken(HttpServletRequest request, String headerName) {
        String bearerToken = request.getHeader(headerName);
        if(bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return Optional.of(bearerToken.substring(7));
        }
        return Optional.empty();
    }

    // 인증 정보를 만들어서 SecurityContext에 저장
    private void setAuthentication(User user) {
        SecurityUser securityUser = SecurityUser.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(user.getPassword())
                .build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                securityUser,
                null,
                null
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
