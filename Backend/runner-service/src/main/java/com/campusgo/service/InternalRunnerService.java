package com.campusgo.service;


import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;


import java.util.Optional;


/** （auth-service / order-service）*/
public interface InternalRunnerService {
    Optional<Runner> findByUsername(String username);
    Optional<Runner> findById(Long id);
    Optional<Runner> findAnyAvailable();
    Runner updateStatus(Long id, RunnerStatus status);
}
