package com.campusgo.service;

import com.campusgo.DTO.UserDTO;

public interface UserService {
    UserDTO findById(Long id);
}
