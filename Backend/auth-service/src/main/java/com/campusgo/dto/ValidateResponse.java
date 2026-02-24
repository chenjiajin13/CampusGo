package com.campusgo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data @AllArgsConstructor
public class ValidateResponse {
    private boolean valid;
    private Long userId;       // when valid is false then null
    private String message;    // Wrong message or OK
    private long expiresAt;    // 0 means null
    private String principalType;
}

