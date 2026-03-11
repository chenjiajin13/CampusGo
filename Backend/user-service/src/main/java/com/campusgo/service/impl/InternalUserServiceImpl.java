package com.campusgo.service.impl;

import com.campusgo.DTO.RegisterRequest;
import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.DTO.UserDTO;
import com.campusgo.mapper.UserMapper;
import com.campusgo.pojo.User;
import com.campusgo.service.InternalUserService;
import com.campusgo.util.UserConverters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class InternalUserServiceImpl implements InternalUserService {

    private final UserMapper userMapper;

    @Override
    public UserAuthDTO findByUsername(String username) {
        User u = userMapper.findByUsername(username);
        return UserConverters.toUserAuthDTO(u);
    }

    @Override
    @Transactional
    public UserAuthDTO register(RegisterRequest req) {
        if (userMapper.findByUsername(req.getUsername()) != null) {
            return null;
        }

        User u = new User();
        u.setUsername(req.getUsername());
        u.setPasswordHash(req.getPasswordHash());
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setAddress(req.getAddress());
        u.setEnabled(true);

        userMapper.insert(u);
        return UserConverters.toUserAuthDTO(u);
    }

    @Override
    public UserDTO findUserDTOById(Long id) {
        User u = userMapper.findById(id);
        return UserConverters.toUserDTO(u);
    }
}
