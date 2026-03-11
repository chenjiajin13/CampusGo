package com.campusgo.controller;

import com.campusgo.dto.AdminDTO;
import com.campusgo.dto.AdminProfileUpdateRequest;
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
    public ResponseEntity<AdminDTO> me(@RequestHeader("X-User-Id") Long userId,
                                       @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (pt == null || !"ADMIN".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        return adminService.findById(userId)
                .map(AdminConverter::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/me")
    public ResponseEntity<AdminDTO> updateMe(@RequestHeader("X-User-Id") Long userId,
                                             @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                             @RequestBody AdminProfileUpdateRequest req) {
        if (pt == null || !"ADMIN".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        AdminDTO dto = AdminConverter.toDTO(adminService.updateBasic(userId, req.getEmail(), req.getPhone()));
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }
}

