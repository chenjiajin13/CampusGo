package com.campusgo.DTO;

import lombok.Data;

@Data
public class UserAuthDTO {
    private Long id;
    private String username;
    private String passwordHash;  // Only internal use
    private String phone;
    private Boolean enabled;
}

