package com.campusgo.service;


import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.VehicleType;


import java.util.List;
import java.util.Optional;


/** public api */
public interface RunnerService {
    Runner create(String username, String rawPassword, String phone, VehicleType vehicleType);
    Optional<Runner> findById(Long id);
    List<Runner> findAll();
    List<Runner> findByStatus(RunnerStatus status);


    Runner updateBasic(Long id, String phone, VehicleType vehicleType);
    Runner updateLocation(Long id, Double lat, Double lng);


    boolean delete(Long id);
}