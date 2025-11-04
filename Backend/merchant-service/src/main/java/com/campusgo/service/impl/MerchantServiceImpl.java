package com.campusgo.service.impl;

import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;
import com.campusgo.mapper.MerchantMapper;
import com.campusgo.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {

    private final MerchantMapper mapper;

    @Override
    @Transactional
    public Merchant create(String username, String rawPassword, String name, String phone, String address,Double lat, Double lng, List<String> tags) {
        Merchant m = Merchant.builder()
                .username(username)
                .passwordHash(BCrypt.hashpw(rawPassword, BCrypt.gensalt()))
                .name(name)
                .phone(phone)
                .address(address)
                .status(MerchantStatus.PAUSED)
                .latitude(null)
                .longitude(null)
                .rating(4.6)           // 与内存版默认对齐
                .finishedOrders(0)
                .tags(List.of())
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        mapper.insert(m); // 回写 id
        return m;
    }

    @Override public Optional<Merchant> findById(Long id) { return mapper.findById(id); }

    @Override public List<Merchant> search(String keyword) { return mapper.search(keyword); }

    @Override public List<Merchant> findAll() { return mapper.findAll(); }

    @Override
    @Transactional
    public Merchant updateBasic(Long id, String phone, String address, List<String> tags) {
        mapper.updateBasic(id, phone, address, tags);
        return mapper.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public Merchant updateStatus(Long id, MerchantStatus status) {
        mapper.updateStatus(id, status);
        return mapper.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public boolean delete(Long id) {
        return mapper.deleteById(id) > 0;
    }
}
