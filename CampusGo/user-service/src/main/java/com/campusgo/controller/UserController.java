package com.campusgo.controller;

import com.campusgo.DTO.RegisterRequest;
import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.DTO.UserDTO;
import com.campusgo.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class UserController {

    private final InternalUserService internalUserService;

    @GetMapping("/by-username/{username}")
    public UserAuthDTO findByUsername(@PathVariable("username") String username) {

        return internalUserService.findByUsername(username);
    }

    @PostMapping("/register")
    public UserAuthDTO register(@RequestBody RegisterRequest req) {
        return internalUserService.register(req);
    }

    @GetMapping("/{id}")
    public UserDTO findById(@PathVariable("id") Long id) {
        return internalUserService.findUserDTOById(id);
    }
}




