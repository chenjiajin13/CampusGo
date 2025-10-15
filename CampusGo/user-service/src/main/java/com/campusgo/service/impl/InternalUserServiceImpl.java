package com.campusgo.service.impl;

import com.campusgo.DTO.RegisterRequest;
import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.DTO.UserDTO;
import com.campusgo.mapper.UserMapper;
import com.campusgo.pojo.User;
import com.campusgo.service.InternalUserService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.security.crypto.bcrypt.BCrypt;

@Service
@RequiredArgsConstructor
public class InternalUserServiceImpl implements InternalUserService {

    /** Mock DB：id -> User */
    private final Map<Long, User> byId = new ConcurrentHashMap<>();
    /** Mock 索引：username(lowercase) -> id */
    private final Map<String, Long> username2Id = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1);

    @PostConstruct
    public void init() {
        // demo 用户：alice / 123456（已加密）
        User demo = new User();
        demo.setId(idGen.getAndIncrement());
        demo.setUsername("alice");
        demo.setPasswordHash(BCrypt.hashpw("123456", BCrypt.gensalt(10)));
        demo.setPhone("18800000000");
        demo.setEmail("alice@example.com");
        demo.setEnabled(true);
        save(demo);
    }

    /** 保存到两张表 */
    private void save(User u) {
        byId.put(u.getId(), u);
        username2Id.put(u.getUsername().toLowerCase(), u.getId());
    }

    private User mustGetById(Long id) {
        User u = byId.get(id);
        if (u == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found: " + id);
        }
        return u;
    }

    private User mustGetByUsername(String username) {
        Long id = username2Id.get(Optional.ofNullable(username).orElse("").toLowerCase());
        if (id == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "user not found: " + username);
        }
        return mustGetById(id);
    }

    @Override
    public UserAuthDTO findByUsername(String username) {
        return UserMapper.toAuthDTO(mustGetByUsername(username));
    }

    @Override
    public UserAuthDTO register(RegisterRequest req) {
        if (req == null || req.getUsername() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid register request");
        }
        String uname = req.getUsername().toLowerCase();
        if (username2Id.containsKey(uname)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "username exists");
        }

        User u = new User();
        u.setId(idGen.getAndIncrement());
        u.setUsername(req.getUsername());
        // 按你的设计：上游已经加密好了，这里直接存 hash
        u.setPasswordHash(req.getPasswordHash());
        u.setPhone(req.getPhone());
        u.setEmail(req.getEmail());
        u.setEnabled(true);

        save(u);
        return UserMapper.toAuthDTO(u);
        // 如果你还需要返回公共信息，可以同时返回 UserDTO；此处保持与你接口一致。
    }

    @Override
    public UserDTO findUserDTOById(Long id) {
        return UserMapper.toPublicDTO(mustGetById(id));
    }
}

