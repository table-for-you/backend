package com.project.tableforyou.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {       // Cors 정책을 벗어나기 위해 사용.
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);   // 내 서버가 응답을 할 때, json을 자바스크립트에서 처리할 수 있게 할지를 성정하는 것.
        config.addAllowedOriginPattern("*");   // 모든 ip에 응답을 허용하겠다.
        config.addAllowedHeader("*");   // 모든 header에 응당읍 허용하겠다.
        config.addAllowedMethod("*");   // 모든 post, get, delete, patch 요청을 허용하겠다.
        source.registerCorsConfiguration("/**", config);    // "/api/**"에 들어오는 모든 주소는 config 설정을 따라라.

        return new CorsFilter(source);
    }
}

