package com.project.tableforyou.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.common.jwt.filter.JwtAuthenticationFilter;
import com.project.tableforyou.common.jwt.filter.JwtExceptionFilter;
import com.project.tableforyou.security.token.service.TokenBlackListService;
import com.project.tableforyou.common.utils.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final UserDetailsService userDetailsService;
    private final TokenBlackListService tokenBlackListService;
    private final CorsConfigurationSource corsConfigurationSource;

    private static final String[] PUBLIC_ENDPOINTS = {
            "/", "/index.html", "/css/**", "/js/**", "/public/**",
            "/api/**","/swagger-resources/**", "/swagger-ui/**",
            "/v3/api-docs/**", "/webjars/**", "/error"
    };
    private static final String[] OWNER_ENDPOINTS = {"/owner/**"};
    private static final String[] ADMIN_ENDPOINTS = {"/admin/**"};

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .cors((cors) -> cors.configurationSource(corsConfigurationSource))

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                                .requestMatchers(OWNER_ENDPOINTS).hasAnyRole("OWNER", "ADMIN")
                                .requestMatchers(ADMIN_ENDPOINTS).hasAnyRole("ADMIN")
                                .anyRequest().authenticated())

                .addFilterBefore(new JwtAuthenticationFilter(jwtUtil, objectMapper, tokenBlackListService, userDetailsService),
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(new JwtExceptionFilter(objectMapper), JwtAuthenticationFilter.class);


        return http.build();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }



}
