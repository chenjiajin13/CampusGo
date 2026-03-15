package com.campusgo.service.impl;

import com.campusgo.DTO.AdminUserUpdateRequest;
import com.campusgo.DTO.UpdatePasswordRequest;
import com.campusgo.DTO.UserDTO;
import com.campusgo.DTO.UserProfileUpdateRequest;
import com.campusgo.mapper.UserMapper;
import com.campusgo.pojo.User;
import com.campusgo.service.UserService;
import com.campusgo.util.UserConverters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.bcrypt.BCrypt;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserDTO findById(Long id) {
        User u = userMapper.findById(id);
        return UserConverters.toUserDTO(u);
    }

    @Override
    public List<UserDTO> findAll() {
        List<User> users = userMapper.findAll();
        if (users == null || users.isEmpty()) {
            return Collections.emptyList();
        }
        return users.stream().map(UserConverters::toUserDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDTO updateProfile(Long id, UserProfileUpdateRequest req) {
        User u = userMapper.findById(id);
        if (u == null) {
            return null;
        }
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setAddress(req.getAddress());
        userMapper.update(u);
        return UserConverters.toUserDTO(userMapper.findById(id));
    }

    @Override
    @Transactional
    public void updatePassword(Long id, UpdatePasswordRequest req) {
        User u = userMapper.findById(id);
        if (u == null) {
            throw new IllegalArgumentException("USER_NOT_FOUND");
        }
        if (req == null || req.getNewPassword() == null || req.getNewPassword().isBlank()) {
            throw new IllegalArgumentException("INVALID_PASSWORD");
        }
        u.setPasswordHash(BCrypt.hashpw(req.getNewPassword(), BCrypt.gensalt()));
        userMapper.update(u);
    }

    @Override
    @Transactional
    public UserDTO adminUpdate(Long id, AdminUserUpdateRequest req) {
        User u = userMapper.findById(id);
        if (u == null) {
            return null;
        }
        if (req.getEmail() != null) {
            u.setEmail(req.getEmail());
        }
        if (req.getPhone() != null) {
            u.setPhone(req.getPhone());
        }
        if (req.getAddress() != null) {
            u.setAddress(req.getAddress());
        }
        if (req.getEnabled() != null) {
            u.setEnabled(req.getEnabled());
        }
        userMapper.update(u);
        return UserConverters.toUserDTO(userMapper.findById(id));
    }

    @Override
    @Transactional
    public boolean deleteById(Long id) {
        return userMapper.deleteById(id) > 0;
    }
}
