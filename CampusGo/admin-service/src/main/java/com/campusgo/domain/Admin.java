package com.campusgo.domain;


import com.campusgo.enums.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Admin {
    private Long id;
    private String username;
    private String passwordHash;
    private String email;
    private String phone;
    private AdminRole role;
    private Boolean enabled;
    private Instant createdAt;
    private Instant updatedAt;
}
