package com.campusgo.service.impl;


import com.campusgo.domain.Runner;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.service.InternalRunnerService;
import com.campusgo.store.InMemoryRunnerStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InternalRunnerServiceImpl implements InternalRunnerService {
    private final InMemoryRunnerStore store;


    @Override public Optional<Runner> findByUsername(String username) { return store.findByUsername(username); }
    @Override public Optional<Runner> findById(Long id) { return store.findById(id); }
    @Override public Optional<Runner> findAnyAvailable() { return store.findAnyAvailable(); }
    @Override public Runner updateStatus(Long id, RunnerStatus status) { return store.updateStatus(id, status); }
}
