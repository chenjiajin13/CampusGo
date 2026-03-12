package com.campusgo.client;

import com.campusgo.dto.TokenResponse;
import feign.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "auth-service", path = "/internal/auth")
public interface AuthClient {

    @PostMapping("/token")
    TokenResponse issue(@RequestParam("userId") Long userId);

    @GetMapping("/validate")
    Response validate(@RequestParam("token") String token);
}
