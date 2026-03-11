package com.campusgo.service;

import com.campusgo.DTO.UserDTO;
import com.campusgo.DTO.UserProfileUpdateRequest;

public interface UserService {
    UserDTO findById(Long id);
    UserDTO updateProfile(Long id, UserProfileUpdateRequest req);
}
