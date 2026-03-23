package com.campusgo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@Configuration
public class GatewayCorsConfig {

    @Value("${CORS_ALLOWED_ORIGIN_PATTERNS:*}")
    private String allowedOriginPatterns;

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(false);

        if (allowedOriginPatterns != null) {
            for (String pattern : allowedOriginPatterns.split(",")) {
                String p = pattern.trim();
                if (!p.isEmpty()) {
                    config.addAllowedOriginPattern(p);
                }
            }
        }
        if (config.getAllowedOriginPatterns() == null || config.getAllowedOriginPatterns().isEmpty()) {
            config.addAllowedOriginPattern("*");
        }

        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }
}
