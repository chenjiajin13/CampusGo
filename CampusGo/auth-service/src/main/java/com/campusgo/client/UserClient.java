package com.campusgo.client;

import com.campusgo.dto.UserAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "user-service", path = "/internal/users")
public interface UserClient {

    @GetMapping("/by-username/{username}")
    UserAuthDTO findByUsername(@PathVariable("username")  String username);

    @PostMapping("/register")
    UserAuthDTO register(@RequestBody UserRegisterReq req);

    // request
    class UserRegisterReq {
        public String username;
        public String passwordHash;
        public String phone;
    }
}

