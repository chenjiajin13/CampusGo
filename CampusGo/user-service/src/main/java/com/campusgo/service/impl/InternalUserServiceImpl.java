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
        // 1) 校验重名
        if (userMapper.findByUsername(req.getUsername()) != null) {
            // 这里可抛业务异常：用户名已存在
            return null;
        }
        // 2) 组装 PO（注意：真实环境请对密码做 hash，这里假设 req 已经是 hash 或者你在此处加密）
        User u = new User();
        u.setUsername(req.getUsername());
        u.setPasswordHash(req.getPasswordHash()); // 或者在这里调用 PasswordEncoder
        u.setEmail(req.getEmail());
        u.setPhone(req.getPhone());
        u.setEnabled(true);

        // 3) 插入
        userMapper.insert(u); // useGeneratedKeys -> id 回写

        // 4) 返回用于鉴权的 DTO
        return UserConverters.toUserAuthDTO(u);
    }

    @Override
    public UserDTO findUserDTOById(Long id) {
        User u = userMapper.findById(id);
        return UserConverters.toUserDTO(u);
    }
}
