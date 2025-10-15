package com.campusgo.service.impl;

import com.campusgo.DTO.UserDTO;
import com.campusgo.mapper.UserMapper;
import com.campusgo.pojo.User;
import com.campusgo.service.UserService;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class UserServiceImpl implements UserService {

        private static final Map<Long, User> DB = new ConcurrentHashMap<>();
        static {
            DB.put(1L, new User(1L, "Alice", "alice@example.com", "alice@example.com", "18800000000", true));
            DB.put(2L, new User(2L, "Bob", "bob@example.com", "bob@example.com", "18800000001", true));
        }

        @Override
        public UserDTO findById(Long id) {
            User u = DB.getOrDefault(id, new User(id, "User-"+id, "u"+id+"@example.com", "u"+id+"@example.com", "18800000000", true));
            return UserMapper.toPublicDTO(u);

    }


}
