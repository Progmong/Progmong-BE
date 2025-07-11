package com.progmong.common.config.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.progmong.api.user.jwt.filter.JwtAuthenticationProcessingFilter;
import com.progmong.api.user.jwt.service.JwtService;
import com.progmong.api.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class JwtConfig {
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;

    @Bean
    public JwtAuthenticationProcessingFilter jwtAuthenticationProcessingFilter() {
        return new JwtAuthenticationProcessingFilter(jwtService, userRepository, objectMapper);
    }
}
