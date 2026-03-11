package com.campusgo.controller;

import com.campusgo.DTO.UserDTO;
import com.campusgo.DTO.UserProfileUpdateRequest;
import com.campusgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserApiController {

    private final UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") Long id) {
        UserDTO dto = userService.findById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@RequestHeader("X-User-Id") Long userId,
                                      @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (pt == null || !"USER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        UserDTO dto = userService.findById(userId);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMe(@RequestHeader("X-User-Id") Long userId,
                                            @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                            @RequestBody UserProfileUpdateRequest req) {
        if (pt == null || !"USER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        UserDTO dto = userService.updateProfile(userId, req);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

}
