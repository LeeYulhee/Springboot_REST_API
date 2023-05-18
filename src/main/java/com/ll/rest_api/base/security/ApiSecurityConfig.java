package com.ll.rest_api.base.security;

import com.ll.rest_api.base.security.entryPoint.ApiAuthenticationEntryPoint;
import com.ll.rest_api.base.security.filter.JwtAuthorizationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class ApiSecurityConfig {
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ApiAuthenticationEntryPoint authenticationEntryPoint;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**") // 아래의 모든 설정은 /api/** 경로에만 적용
                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .authenticationEntryPoint(authenticationEntryPoint)
                )
                .authorizeHttpRequests(
                        authorizeHttpRequests -> authorizeHttpRequests
                                .requestMatchers("/api/*/member/login").permitAll() // 로그인은 누구나 가능
                                .anyRequest().authenticated() // 나머지는 인증된 사용자만 가능
                )
                // .authorizeHttpRequests... -> 어디까지 접근 허용할지 정하는 메서드
                .cors().disable() // 타 도메인에서 API 호출 가능
                .csrf().disable() // CSRF 토큰 끄기
                .httpBasic().disable() // httpBaic 로그인 방식 끄기
                .formLogin().disable() // 폼 로그인 방식 끄기
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(STATELESS)
                ) // 세션끄기 => 위 과정은 REST API에 필요 없는 기능들 끈 것
                .addFilterBefore(
                        jwtAuthorizationFilter, // 엑세스 토큰으로 부터 로그인 처리 => 헤더에 AccessToken이 있다면 자동 로그인
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }
    // PasswordEncoder를 여기에 넣으면 오류 발생(클래스 생성해서 옮기기)
}