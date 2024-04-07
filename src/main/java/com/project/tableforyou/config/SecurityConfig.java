package com.project.tableforyou.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.tableforyou.handler.authFailureHandler.CustomAuthFailureHandler;
import com.project.tableforyou.handler.logoutHandler.CustomLogoutHandler;
import com.project.tableforyou.jwt.filter.JwtAuthenticationFilter;
import com.project.tableforyou.jwt.filter.JwtAuthorizationFilter;
import com.project.tableforyou.jwt.handler.OAuth2SuccessHandler;
import com.project.tableforyou.jwt.util.JwtUtil;
import com.project.tableforyou.refreshToken.service.RefreshTokenService;
import com.project.tableforyou.security.auth.PrincipalDetailsService;
import com.project.tableforyou.security.oauth.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
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
    private final RefreshTokenService refreshTokenService;
    private final CustomLogoutHandler customLogoutHandler;
    private final PrincipalDetailsService principalDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

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
                                .requestMatchers("/manager/**").hasAnyRole("MANAGER", "ADMIN")
                                .requestMatchers("/admin/**").hasAnyRole("ADMIN")
                                .anyRequest().permitAll())


                .oauth2Login(oauth2 ->
                        oauth2
                                .userInfoEndpoint(endPoint ->
                                        endPoint.userService(principalOAuth2UserService))
                                .successHandler(oAuth2SuccessHandler))

                .addFilterAt(new JwtAuthenticationFilter(
                        authenticationManager(authenticationConfiguration), jwtUtil, customAuthFailureHandler, refreshTokenService, objectMapper),
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil, objectMapper), UsernamePasswordAuthenticationFilter.class)

                .logout(logout ->
                        logout.addLogoutHandler(customLogoutHandler));


        return http.build();
    }

    /* DaoAuthenticationProvider 등록 */
    protected void addAuthenticationProvider(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(daoAuthenticationProvider());
    }

    /* DaoAuthenticationProvider 구성 */
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider() {

        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(principalDetailsService);
        authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);
        authenticationProvider.setHideUserNotFoundExceptions(false);
        // hideUserNotFoundExceptions를 false하며 UsernameNotFoundException 활성화
        return  authenticationProvider;
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }



}
