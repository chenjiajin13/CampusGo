package com.campusgo.util;

import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.DTO.UserDTO;
import com.campusgo.pojo.User;

public class UserConverters {

    public static UserDTO toUserDTO(User u) {
        if (u == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setEnabled(u.getEnabled());
        return dto;
    }

    public static UserAuthDTO toUserAuthDTO(User u) {
        if (u == null) return null;
        UserAuthDTO dto = new UserAuthDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setPasswordHash(u.getPasswordHash()); // 内部鉴权需要 hash
        dto.setEnabled(u.getEnabled());
        return dto;
    }
}
