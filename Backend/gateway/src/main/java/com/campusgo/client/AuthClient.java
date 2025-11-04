package com.campusgo.client;

import com.campusgo.dto.TokenResponse;
import com.campusgo.dto.ValidateResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", path = "/internal/auth")
public interface AuthClient {

    @PostMapping("/token")
    TokenResponse issue(@RequestParam("userId") Long userId);

    @GetMapping("/validate")
    ValidateResponse validate(@RequestParam("token") String token);
}
