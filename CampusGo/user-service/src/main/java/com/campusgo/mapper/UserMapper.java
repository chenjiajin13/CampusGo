package com.campusgo.mapper;

import com.campusgo.DTO.UserDTO;
import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.pojo.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    // for outside use
    public static UserDTO toPublicDTO(User u) {
        if (u == null) return null;
        UserDTO dto = new UserDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setEmail(u.getEmail());
        dto.setPhone(u.getPhone());
        dto.setEnabled(u.getEnabled());
        return dto;
    }

    // for  auth-service
    public static UserAuthDTO toAuthDTO(User u) {
        if (u == null) return null;
        UserAuthDTO dto = new UserAuthDTO();
        dto.setId(u.getId());
        dto.setUsername(u.getUsername());
        dto.setPasswordHash(u.getPasswordHash());
        dto.setPhone(u.getPhone());
        dto.setEnabled(Boolean.TRUE.equals(u.getEnabled()));
        return dto;
    }
}


