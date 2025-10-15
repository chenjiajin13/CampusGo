package com.campusgo.service.impl;


import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;
import com.campusgo.service.InternalMerchantService;
import com.campusgo.store.InMemoryMerchantStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.Optional;


@Service
@RequiredArgsConstructor
public class InternalMerchantServiceImpl implements InternalMerchantService {
    private final InMemoryMerchantStore store;


    @Override public Optional<Merchant> findByUsername(String username) { return store.findByUsername(username); }
    @Override public Optional<Merchant> findById(Long id) { return store.findById(id); }
    @Override public Merchant updateStatus(Long id, MerchantStatus status) { return store.updateStatus(id, status); }
}
