package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TokenPairResponse {
    private String accessToken;
    private long   accessExpiresAt;   // epoch seconds
    private String refreshToken;
    private long   refreshExpiresAt;  // epoch seconds
}

