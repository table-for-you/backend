package com.project.tableforyou.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.config.auth.PrincipalDetailsService;
import com.project.tableforyou.config.handler.CustomAuthFailureHandler;
import com.project.tableforyou.config.handler.CustomAuthSuccessHandler;
import com.project.tableforyou.config.handler.CustomLogoutHandler;
import com.project.tableforyou.config.oauth.PrincipalOAuth2UserService;
import com.project.tableforyou.jwt.JwtUtil;
import com.project.tableforyou.jwt.filter.JwtAuthenticationFilter;
import com.project.tableforyou.jwt.filter.JwtAuthorizationFilter;
import com.project.tableforyou.jwt.handler.OAuth2SuccessHandler;
import com.project.tableforyou.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final AuthService authService;
    private final CustomLogoutHandler customLogoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .addFilter(corsFilter)

                .formLogin(AbstractHttpConfigurer::disable)

                .httpBasic(AbstractHttpConfigurer::disable)

                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/api/user/**").authenticated()
                                .requestMatchers("/manager/**").hasAnyRole("manager", "admin")
                                .requestMatchers("/admin/**").hasAnyRole("admin")
                                .anyRequest().permitAll())


                .oauth2Login(oauth2 ->
                        oauth2
                                .userInfoEndpoint(endPoint ->
                                        endPoint.userService(principalOAuth2UserService))
                                .successHandler(oAuth2SuccessHandler))

                .addFilterAt(new JwtAuthenticationFilter(
                        authenticationManager(authenticationConfiguration), jwtUtil, customAuthFailureHandler, authService, objectMapper),
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                .logout(logout ->
                        logout.addLogoutHandler(customLogoutHandler));


        return http.build();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }
}
