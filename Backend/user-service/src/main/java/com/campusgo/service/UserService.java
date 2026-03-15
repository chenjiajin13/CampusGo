package com.campusgo.service;

import com.campusgo.DTO.AdminUserUpdateRequest;
import com.campusgo.DTO.UpdatePasswordRequest;
import com.campusgo.DTO.UserDTO;
import com.campusgo.DTO.UserProfileUpdateRequest;

import java.util.List;

public interface UserService {
    UserDTO findById(Long id);
    List<UserDTO> findAll();
    UserDTO updateProfile(Long id, UserProfileUpdateRequest req);
    void updatePassword(Long id, UpdatePasswordRequest req);
    UserDTO adminUpdate(Long id, AdminUserUpdateRequest req);
    boolean deleteById(Long id);
}
