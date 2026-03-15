package com.campusgo.DTO;

import lombok.Data;

@Data
public class AdminUserUpdateRequest {
    private String email;
    private String phone;
    private String address;
    private Boolean enabled;
}
