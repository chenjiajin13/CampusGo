package com.campusgo.store;


import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.VehicleType;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;


@Component
public class InMemoryRunnerStore {
    private final Map<Long, Runner> store = new ConcurrentHashMap<>();
    private final AtomicLong idGen = new AtomicLong(1000);


    @PostConstruct
    public void initMock() {
        //mock data
        create("alice_runner", "pass123", "88880001", VehicleType.BICYCLE);
        create("bob_runner", "pass123", "88880002", VehicleType.MOTORBIKE);
        create("charlie_runner", "pass123", "88880003", VehicleType.E_SCOOTER);
        // status & position
        updateStatus(1000L, RunnerStatus.AVAILABLE);
        updateLocation(1000L, 1.2966, 103.7764);
        updateStatus(1001L, RunnerStatus.BUSY);
        updateLocation(1001L, 1.3000, 103.7700);
        updateStatus(1002L, RunnerStatus.AVAILABLE);
        updateLocation(1002L, 1.3100, 103.7800);
    }


    public Runner create(String username, String rawPassword, String phone, VehicleType vehicleType) {
        long id = idGen.getAndIncrement();
        Instant now = Instant.now();
        Runner r = Runner.builder()
                .id(id)
                .username(username)
                .passwordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                .phone(phone)
                .vehicleType(vehicleType)
                .status(RunnerStatus.OFFLINE)
                .latitude(null)
                .longitude(null)
                .rating(5.0)
                .completedOrders(0)
                .totalEarningsCents(0L)
                .createdAt(now)
                .updatedAt(now)
                .build();
        store.put(id, r);
        return r;
    }
    public Optional<Runner> findById(Long id) { return Optional.ofNullable(store.get(id)); }
    public Optional<Runner> findByUsername(String username) {
        return store.values().stream().filter(r -> r.getUsername().equalsIgnoreCase(username)).findFirst();
    }
    public List<Runner> findAll() { return new ArrayList<>(store.values()); }
    public List<Runner> findByStatus(RunnerStatus status) {
        List<Runner> res = new ArrayList<>();
        for (Runner r : store.values()) if (r.getStatus() == status) res.add(r);
        return res;
    }


    public Runner updateBasic(Long id, String phone, VehicleType vehicleType) {
        Runner r = store.get(id);
        if (r == null) throw new NoSuchElementException("Runner not found");
        r.setPhone(phone);
        r.setVehicleType(vehicleType);
        r.setUpdatedAt(Instant.now());
        return r;
    }
    public Runner updateStatus(Long id, RunnerStatus status) {
        Runner r = store.get(id);
        if (r == null) throw new NoSuchElementException("Runner not found");
        r.setStatus(status);
        r.setUpdatedAt(Instant.now());
        return r;
    }
    public Runner updateLocation(Long id, Double lat, Double lng) {
        Runner r = store.get(id);
        if (r == null) throw new NoSuchElementException("Runner not found");
        r.setLatitude(lat);
        r.setLongitude(lng);
        r.setUpdatedAt(Instant.now());
        return r;
    }
    public boolean delete(Long id) { return store.remove(id) != null; }


    public Optional<Runner> findAnyAvailable() {
        return store.values().stream().filter(r -> r.getStatus() == RunnerStatus.AVAILABLE).findFirst();
    }
}
