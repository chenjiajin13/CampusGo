package com.campusgo.dto;


import com.campusgo.enums.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDTO {
    private Long id;
    private String username;
    private String email;
    private String phone;
    private AdminRole role;
    private Boolean enabled;
}
