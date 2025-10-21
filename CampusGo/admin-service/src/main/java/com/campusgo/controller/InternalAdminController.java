package com.campusgo.controller;

import com.campusgo.domain.Admin;
import com.campusgo.service.AdminService;


import com.campusgo.dto.AdminAuthDTO;
import com.campusgo.dto.AdminCreateRequest;
import com.campusgo.dto.AdminDTO;
import com.campusgo.dto.UpdateStatusRequest;
import com.campusgo.enums.AdminRole;
import com.campusgo.mapper.AdminConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/internal/admins")
@RequiredArgsConstructor

public class InternalAdminController {


    private final AdminService service;


    // for Auth-service
    // Search admin by username
    @GetMapping("/by-username/{username}")
    public ResponseEntity<AdminAuthDTO> findByUsername(@PathVariable("username") String username) {
        return service.findByUsername(username)
                .map(AdminConverter::toAuthDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Construct a new admin
    @PostMapping
    public AdminDTO create(@RequestBody AdminCreateRequest req) {
        Admin a = service.create(req.getUsername(), req.getPassword(), req.getEmail(), req.getPhone(), req.getRole() == null ? AdminRole.OPERATOR : req.getRole());
        return AdminConverter.toDTO(a);
    }


    // Search admin by id
    @GetMapping("/{id}")
    public ResponseEntity<AdminDTO> get(@PathVariable("id") Long id) {
        return service.findById(id)
                .map(AdminConverter::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // get all admin
    @GetMapping
    public List<AdminDTO> list() {
        return service.findAll().stream().map(AdminConverter::toDTO).collect(Collectors.toList());
    }


    // update admin status
    @PatchMapping("/{id}/status")
    public ResponseEntity<AdminDTO> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
        if (req == null || req.getEnabled() == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(AdminConverter.toDTO(service.updateStatus(id, req.getEnabled())));
    }


    // delete admin
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}