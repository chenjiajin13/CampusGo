package com.campusgo.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "auth.jwt")
public class JwtProps {
    private String issuer;
    private String secret;
    private int expiresMinutes;

}
