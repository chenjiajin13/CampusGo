package com.campusgo.DTO;

import lombok.Data;

@Data
public class UserProfileUpdateRequest {
    private String email;
    private String phone;
    private String address;
}

