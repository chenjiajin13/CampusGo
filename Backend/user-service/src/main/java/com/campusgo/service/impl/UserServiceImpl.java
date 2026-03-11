package com.campusgo.service.impl;

import com.campusgo.DTO.UserDTO;
import com.campusgo.DTO.UserProfileUpdateRequest;
import com.campusgo.mapper.UserMapper;
import com.campusgo.pojo.User;
import com.campusgo.service.UserService;
import com.campusgo.util.UserConverters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
