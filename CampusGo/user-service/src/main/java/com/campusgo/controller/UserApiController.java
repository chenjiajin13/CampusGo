package com.campusgo.controller;

import com.campusgo.DTO.UserDTO;
import com.campusgo.mapper.UserMapper;
import com.campusgo.pojo.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserApiController {

    @GetMapping("/{id}")
    public UserDTO getUser(@PathVariable("id") Long id) {
        // mock database
        User user = new User();
        user.setId(id);
        user.setUsername("Alice-" + id);
        user.setEmail("alice@example.com");
        user.setPhone("18800000000");
        user.setEnabled(true);
        return UserMapper.toPublicDTO(user);
    }
}

