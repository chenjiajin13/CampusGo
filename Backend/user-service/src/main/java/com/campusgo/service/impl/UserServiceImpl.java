package com.campusgo.service.impl;

import com.campusgo.DTO.UserDTO;
import com.campusgo.mapper.UserMapper;
import com.campusgo.pojo.User;
import com.campusgo.service.UserService;
import com.campusgo.util.UserConverters;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;

    @Override
    public UserDTO findById(Long id) {
        User u = userMapper.findById(id);
        // 也可抛 NotFound 异常，由 @ControllerAdvice 统一处理
        return UserConverters.toUserDTO(u);
    }
}
