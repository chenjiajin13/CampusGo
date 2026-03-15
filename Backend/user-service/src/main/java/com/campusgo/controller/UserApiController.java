package com.campusgo.controller;

import com.campusgo.DTO.AdminUserUpdateRequest;
import com.campusgo.DTO.UpdatePasswordRequest;
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

    private boolean isAdmin(String pt) {
        return pt != null && "ADMIN".equalsIgnoreCase(pt);
    }
    @GetMapping("/{id:\\d+}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") Long id) {
        UserDTO dto = userService.findById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                      @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"USER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        UserDTO dto = userService.findById(userId);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<?> list(@RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!isAdmin(pt)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(userService.findAll());
    }

    @PutMapping("/me")
    public ResponseEntity<UserDTO> updateMe(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                            @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                            @RequestBody UserProfileUpdateRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"USER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        UserDTO dto = userService.updateProfile(userId, req);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                 @RequestBody UpdatePasswordRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"USER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        userService.updatePassword(userId, req);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id:\\d+}")
    public ResponseEntity<UserDTO> updateByAdmin(@PathVariable("id") Long id,
                                                 @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                 @RequestBody AdminUserUpdateRequest req) {
        if (!isAdmin(pt)) {
            return ResponseEntity.status(403).build();
        }
        UserDTO dto = userService.adminUpdate(id, req);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> deleteByAdmin(@PathVariable("id") Long id,
                                              @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (!isAdmin(pt)) {
            return ResponseEntity.status(403).build();
        }
        boolean ok = userService.deleteById(id);
        return ok ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
