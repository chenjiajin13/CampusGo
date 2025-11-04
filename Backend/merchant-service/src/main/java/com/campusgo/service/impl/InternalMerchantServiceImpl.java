package com.campusgo.service.impl;

import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;
import com.campusgo.mapper.MerchantMapper;
import com.campusgo.service.InternalMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class InternalMerchantServiceImpl implements InternalMerchantService {

    private final MerchantMapper mapper;

    @Override public Optional<Merchant> findByUsername(String username) { return mapper.findByUsername(username); }

    @Override public Optional<Merchant> findById(Long id) { return mapper.findById(id); }

    @Override
    @Transactional
    public Merchant updateStatus(Long id, MerchantStatus status) {
        mapper.updateStatus(id, status);
        return mapper.findById(id).orElse(null);
    }
}
