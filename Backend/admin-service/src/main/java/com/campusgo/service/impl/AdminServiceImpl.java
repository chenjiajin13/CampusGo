package com.campusgo.service.impl;

import com.campusgo.domain.Admin;
import com.campusgo.enums.AdminRole;
import com.campusgo.mapper.AdminMapper;
import com.campusgo.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final AdminMapper mapper;

    @Override
    @Transactional
    public Admin create(String username, String rawPassword, String email, String phone, AdminRole role) {
        Admin a = Admin.builder()
                .username(username)
                .passwordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                .email(email)
                .phone(phone)
                .role(role)
                .enabled(false)               // 与 InMemory 行为一致：初始禁用
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        mapper.insert(a);                      // 回写自增 id
        return a;
    }

    @Override public Optional<Admin> findById(Long id) { return Optional.ofNullable(mapper.findById(id)); }

    @Override public Optional<Admin> findByUsername(String username) { return Optional.ofNullable(mapper.findByUsername(username)); }

    @Override public List<Admin> findAll() { return mapper.findAll(); }

    @Override
    @Transactional
    public Admin updateStatus(Long id, Boolean enabled) {
        mapper.updateStatus(id, enabled);
        return Optional.ofNullable(mapper.findById(id)).orElse(null);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return mapper.deleteById(id) > 0;
    }
}
