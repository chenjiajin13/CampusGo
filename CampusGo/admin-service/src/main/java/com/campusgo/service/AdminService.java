package com.campusgo.service;


import com.campusgo.domain.Admin;
import com.campusgo.enums.AdminRole;


import java.util.List;
import java.util.Optional;


public interface AdminService {
    Admin create(String username, String rawPassword, String email, String phone, AdminRole role);
    Optional<Admin> findById(Long id);
    Optional<Admin> findByUsername(String username);
    List<Admin> findAll();
    Admin updateStatus(Long id, Boolean enabled);
    boolean delete(Long id);
}