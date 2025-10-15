package com.campusgo.service;

import com.campusgo.DTO.RegisterRequest;
import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.DTO.UserDTO;

public interface InternalUserService {
    UserAuthDTO findByUsername(String username);
    UserAuthDTO register(RegisterRequest req);
    UserDTO findUserDTOById(Long id);
}
