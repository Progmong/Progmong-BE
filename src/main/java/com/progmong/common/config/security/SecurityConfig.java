package com.progmong.common.config.security;

import com.progmong.common.config.jwt.JwtConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtConfig jwtConfig;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .cors(corsCustomizer -> corsCustomizer.configurationSource(request -> {
                    CorsConfiguration config = new CorsConfiguration();
                    config.setAllowedOrigins(Arrays.asList(
                            "http://localhost:5200",
                            "http://localhost:5173"
                    ));
                    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH","DELETE", "OPTIONS"));
                    config.setAllowCredentials(true);
                    config.setAllowedHeaders(Arrays.asList("Authorization", "Authorization_refresh","Content-Type", "X-Requested-With", "Accept", "Origin"));
                    config.setMaxAge(3600L);
                    config.addExposedHeader("Authorization");
                    config.addExposedHeader("Authorization_refresh");
                    return config;
                }))
                .headers(headers -> headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::disable))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(
                                "/api-doc", "/health", "/v3/api-docs/**",
                                "/swagger-resources/**","/swagger-ui/**",
                                "/h2-console/**"
                        ).permitAll() // 스웨거, H2, healthCheck 허가
                        .requestMatchers("/api/v1/users/login",
                                "/api/v1/users/register",
                                "/api/v1/solvedac/**",
                                "/api/v1/users/verify-email",
                                "/api/v1/users/verification-email-code",
                                "/api/v1/users/reset-password/request",
                                "/api/v1/users/reset-password/confirm")
                        .permitAll()
                        // 로그인, 회원가입, 이메일인증 허가
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptionHandling ->
                        exceptionHandling.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED)));

        http.addFilterBefore(jwtConfig.jwtAuthenticationProcessingFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder());
        return new ProviderManager(provider);
    }
}

