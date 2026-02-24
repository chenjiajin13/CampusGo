package com.campusgo.controller;

import com.campusgo.dto.*;
import com.campusgo.service.AuthFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthPublicController {

    private final AuthFacade facade;

    @PostMapping(
            value = "/login",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<TokenPairResponse> login(@RequestBody LoginRequest req) {
        return ResponseEntity.ok(facade.login(req));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenPairResponse> refresh(@RequestParam("refreshToken") String refreshToken) {
        return ResponseEntity.ok(facade.refresh(refreshToken));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("refreshToken") String refreshToken) {
        facade.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(
            value = "/register",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserAuthDTO> register(@RequestBody RegisterRequest req) {
        return ResponseEntity.ok(facade.register(req));
    }

    @PostMapping("/merchant/login")
    public TokenPairResponse merchantLogin(@RequestBody LoginRequest req) {
        return facade.loginMerchant(req);
    }

    @PostMapping("/runner/login")
    public TokenPairResponse runnerLogin(@RequestBody LoginRequest req) {
        return facade.loginRunner(req);
    }

    @PostMapping("/admin/login")
    public TokenPairResponse adminLogin(@RequestBody LoginRequest req) {
        return facade.loginAdmin(req);
    }
}

