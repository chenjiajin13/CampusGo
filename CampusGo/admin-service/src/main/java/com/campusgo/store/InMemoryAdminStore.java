package com.campusgo.store;


import com.campusgo.domain.Admin;
import com.campusgo.enums.AdminRole;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class InMemoryAdminStore {


    private final Map<Long, Admin> byId = new ConcurrentHashMap<>();
    private final Map<String, Long> username2Id = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(3000);


    @PostConstruct
    public void initMock() {
// Mock data
        create("super_admin", "admin123", "super@campusgo.com", "88889999", AdminRole.SUPER_ADMIN);
        create("operator", "admin123", "op@campusgo.com", "88887777", AdminRole.OPERATOR);
        create("auditor", "admin123", "audit@campusgo.com", "88886666", AdminRole.AUDITOR);


        updateStatus(3000L, true);
        updateStatus(3001L, true);
        updateStatus(3002L, false);
    }


    public Admin create(String username, String rawPassword, String email, String phone, AdminRole role) {
        long id = idGen.getAndIncrement();
        Instant now = Instant.now();
        Admin a = Admin.builder()
                .id(id)
                .username(username)
                .passwordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                .email(email)
                .phone(phone)
                .role(role)
                .enabled(false)
                .createdAt(now)
                .updatedAt(now)
                .build();
        byId.put(id, a);
        username2Id.put(username.toLowerCase(), id);
        return a;
    }


    public Optional<Admin> findById(Long id) {
        return Optional.ofNullable(byId.get(id));
    }


    public Optional<Admin> findByUsername(String username) {
        Long id = username2Id.get(Optional.ofNullable(username).orElse("").toLowerCase());
        return id == null ? Optional.empty() : findById(id);
    }


    public List<Admin> findAll() {
        return new ArrayList<>(byId.values());
    }


    public Admin updateStatus(Long id, Boolean enabled) {
        Admin a = byId.get(id);
        if (a == null) throw new NoSuchElementException("Admin not found");
        a.setEnabled(enabled);
        a.setUpdatedAt(Instant.now());
        return a;
    }
    public boolean delete(Long id) { return byId.remove(id) != null; }
}