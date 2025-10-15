package com.campusgo.service.impl;


import com.campusgo.domain.Admin;
import com.campusgo.enums.AdminRole;
import com.campusgo.service.AdminService;
import com.campusgo.store.InMemoryAdminStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {
    private final InMemoryAdminStore store;


    @Override public Admin create(String username, String rawPassword, String email, String phone, AdminRole role) { return store.create(username, rawPassword, email, phone, role); }
    @Override public Optional<Admin> findById(Long id) { return store.findById(id); }
    @Override public Optional<Admin> findByUsername(String username) { return store.findByUsername(username); }
    @Override public List<Admin> findAll() { return store.findAll(); }
    @Override public Admin updateStatus(Long id, Boolean enabled) { return store.updateStatus(id, enabled); }
    @Override public boolean delete(Long id) { return store.delete(id); }
}
