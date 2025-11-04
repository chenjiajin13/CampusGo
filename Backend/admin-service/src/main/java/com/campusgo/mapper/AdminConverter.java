package com.campusgo.mapper;


import com.campusgo.domain.Admin;
import com.campusgo.dto.AdminAuthDTO;
import com.campusgo.dto.AdminDTO;


public class AdminConverter {
    public static AdminDTO toDTO(Admin a) {
        if (a == null) return null;
        return AdminDTO.builder()
                .id(a.getId())
                .username(a.getUsername())
                .email(a.getEmail())
                .phone(a.getPhone())
                .role(a.getRole())
                .enabled(a.getEnabled())
                .build();
    }


    public static AdminAuthDTO toAuthDTO(Admin a) {
        if (a == null) return null;
        return AdminAuthDTO.builder()
                .id(a.getId())
                .username(a.getUsername())
                .passwordHash(a.getPasswordHash())
                .role("ROLE_ADMIN")
                .build();
    }
}
