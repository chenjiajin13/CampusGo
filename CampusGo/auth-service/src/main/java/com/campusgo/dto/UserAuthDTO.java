package com.campusgo.dto;

import lombok.Data;

@Data
public class UserAuthDTO {
    private Long id;
    private String username;
    private String passwordHash;
    private String phone;
    private Boolean enabled;
}
