package com.campusgo.controller;

import com.campusgo.dto.AdminDTO;
import com.campusgo.dto.AdminProfileUpdateRequest;
import com.campusgo.dto.UpdatePasswordRequest;
import com.campusgo.mapper.AdminConverter;
import com.campusgo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
public class PublicAdminController {

    private final AdminService adminService;

    @GetMapping("/me")
    public ResponseEntity<AdminDTO> me(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                       @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"ADMIN".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        return adminService.findById(userId)
                .map(AdminConverter::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<AdminDTO> updateMe(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                             @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                             @RequestBody AdminProfileUpdateRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"ADMIN".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        AdminDTO dto = AdminConverter.toDTO(adminService.updateBasic(userId, req.getEmail(), req.getPhone()));
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                 @RequestBody UpdatePasswordRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"ADMIN".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        adminService.updatePassword(userId, req.getNewPassword());
        return ResponseEntity.noContent().build();
    }
}
