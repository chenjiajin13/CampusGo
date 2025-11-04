package com.campusgo.service.impl;

import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.mapper.RunnerMapper;
import com.campusgo.service.InternalRunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InternalRunnerServiceImpl implements InternalRunnerService {

    private final RunnerMapper mapper;

    @Override
    public Optional<Runner> findByUsername(String username) {
        return mapper.findByUsername(username);
    }

    @Override
    public Optional<Runner> findById(Long id) {
        return mapper.findById(id);
    }

    @Override
    public Optional<Runner> findAnyAvailable() {
        return mapper.findAnyAvailable();
    }

    @Override
    @Transactional
    public Runner updateStatus(Long id, RunnerStatus status) {
        mapper.updateStatus(id, status);
        return mapper.findById(id).orElse(null);
    }
}
