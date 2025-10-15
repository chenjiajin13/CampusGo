package com.campusgo.service.impl;


import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.VehicleType;
import com.campusgo.service.RunnerService;
import com.campusgo.store.InMemoryRunnerStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class RunnerServiceImpl implements RunnerService {
    private final InMemoryRunnerStore store;


    @Override public Runner create(String username, String rawPassword, String phone, VehicleType vehicleType) {
        return store.create(username, rawPassword, phone, vehicleType);
    }
    @Override public Optional<Runner> findById(Long id) { return store.findById(id); }
    @Override public List<Runner> findAll() { return store.findAll(); }
    @Override public List<Runner> findByStatus(RunnerStatus status) { return store.findByStatus(status); }
    @Override public Runner updateBasic(Long id, String phone, VehicleType vehicleType) { return store.updateBasic(id, phone, vehicleType); }
    @Override public Runner updateLocation(Long id, Double lat, Double lng) { return store.updateLocation(id, lat, lng); }
    @Override public boolean delete(Long id) { return store.delete(id); }
}
