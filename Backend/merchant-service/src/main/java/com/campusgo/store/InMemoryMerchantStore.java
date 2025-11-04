package com.campusgo.store;


import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;


import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class InMemoryMerchantStore {
    private final Map<Long, Merchant> byId = new ConcurrentHashMap<>();
    private final Map<String, Long> username2Id = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(2000);


    @PostConstruct
    public void initMock() {
        create("kfc_owner", "pass123", "KFC-NUS", "88886666", "COM2, NUS", 1.2965, 103.7761, Arrays.asList("chicken","set meal"));
        create("mac_owner", "pass123", "McD-NUS", "88887777", "UTown, NUS", 1.3050, 103.7730, Arrays.asList("burger","fries"));
        updateStatus(2000L, MerchantStatus.OPEN);
        updateStatus(2001L, MerchantStatus.OPEN);
    }


    public Merchant create(String username, String rawPassword, String name, String phone, String address,
                           Double lat, Double lng, List<String> tags) {
        long id = idGen.getAndIncrement();
        Instant now = Instant.now();
        Merchant m = Merchant.builder()
                .id(id)
                .username(username)
                .passwordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                .name(name)
                .phone(phone)
                .address(address)
                .status(MerchantStatus.PAUSED)
                .latitude(lat)
                .longitude(lng)
                .rating(4.6)
                .finishedOrders(0)
                .tags(tags)
                .createdAt(now)
                .updatedAt(now)
                .build();
        byId.put(id, m);
        username2Id.put(username.toLowerCase(), id);
        return m;
    }


    public Optional<Merchant> findById(Long id) { return Optional.ofNullable(byId.get(id)); }


    public Optional<Merchant> findByUsername(String username) {
        Long id = username2Id.get(Optional.ofNullable(username).orElse("").toLowerCase());
        return id == null ? Optional.empty() : findById(id);
    }
    public List<Merchant> findAll() { return new ArrayList<>(byId.values()); }


    public List<Merchant> searchByKeyword(String keyword) {
        if (keyword == null || keyword.isEmpty()) return findAll();
        String k = keyword.toLowerCase();
        List<Merchant> res = new ArrayList<>();
        for (Merchant m : byId.values()) {
            if ((m.getName() != null && m.getName().toLowerCase().contains(k)) ||
                    (m.getTags() != null && m.getTags().toString().toLowerCase().contains(k)) ||
                    (m.getAddress() != null && m.getAddress().toLowerCase().contains(k))) {
                res.add(m);
            }
        }
        return res;
    }


    public Merchant updateBasic(Long id, String phone, String address, List<String> tags) {
        Merchant m = byId.get(id); if (m == null) throw new NoSuchElementException("Merchant not found");
        m.setPhone(phone); m.setAddress(address); m.setTags(tags);
        m.setUpdatedAt(Instant.now()); return m;
    }


    public Merchant updateStatus(Long id, MerchantStatus status) {
        Merchant m = byId.get(id); if (m == null) throw new NoSuchElementException("Merchant not found");
        m.setStatus(status); m.setUpdatedAt(Instant.now()); return m;
    }


    public boolean delete(Long id) { return byId.remove(id) != null; }
}