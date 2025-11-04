package com.campusgo.service.impl;

import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.VehicleType;
import com.campusgo.mapper.RunnerMapper;
import com.campusgo.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RunnerServiceImpl implements RunnerService {

    private final RunnerMapper mapper;

    @Override
    @Transactional
    public Runner create(String username, String rawPassword, String phone, VehicleType vehicleType) {
        Runner r = Runner.builder()
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
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        mapper.insert(r); // id 回写
        return r;
    }

    @Override
    public Optional<Runner> findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public List<Runner> findAll() {
        return mapper.findAll();
    }

    @Override
    public List<Runner> findByStatus(RunnerStatus status) {
        return mapper.findByStatus(status);
    }

    @Override
    @Transactional
    public Runner updateBasic(Long id, String phone, VehicleType vehicleType) {
        mapper.updateBasic(id, phone, vehicleType);
        return mapper.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Runner updateLocation(Long id, Double lat, Double lng) {
        mapper.updateLocation(id, lat, lng);
        return mapper.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return mapper.deleteById(id) > 0;
    }
}
