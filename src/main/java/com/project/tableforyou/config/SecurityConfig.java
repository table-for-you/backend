package com.project.tableforyou.config;

import com.project.tableforyou.config.auth.PrincipalDetailsService;
import com.project.tableforyou.config.handler.CustomAuthFailureHandler;
import com.project.tableforyou.config.oauth.PrincipalOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsFilter corsFilter;
    private final PrincipalOAuth2UserService principalOAuth2UserService;
    private final PrincipalDetailsService principalDetailsService;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.
                csrf(AbstractHttpConfigurer::disable)
                .addFilter(corsFilter)
                .authorizeHttpRequests(authorize ->
                        authorize
                                .requestMatchers("/user/**").authenticated()
                                .requestMatchers("/manager/**").hasAnyRole("manager", "admin")
                                .requestMatchers("/admin/**").hasAnyRole("admin")
                                .anyRequest().permitAll())
                .formLogin(formLogin ->
                        formLogin
                                .loginPage("/login")
                                .loginProcessingUrl("/loginProc")
                                .failureHandler(new CustomAuthFailureHandler())
                                .defaultSuccessUrl("/"))
                .logout(logout ->       // 안해도 logout되긴 함. 추가적인 정보를 위해 넣은 것.
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/login")
                                .invalidateHttpSession(true) // 세션을 무효화
                                .deleteCookies("JSESSIONID"))
                .oauth2Login(oauth2 ->
                        oauth2
                                .loginPage("/api/login")
                                .userInfoEndpoint(endPoint ->
                                        endPoint.userService(principalOAuth2UserService)))
                .rememberMe(rememberMe ->
                        rememberMe
                                .key("my-secret-key")
                                .rememberMeParameter("rememberMe")
                                .tokenValiditySeconds(30*24*60*60)
                                .userDetailsService(principalDetailsService));

        return http.build();
    }
}
