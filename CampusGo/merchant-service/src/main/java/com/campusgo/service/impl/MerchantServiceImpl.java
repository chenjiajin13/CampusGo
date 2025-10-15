package com.campusgo.service.impl;


import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;
import com.campusgo.service.MerchantService;
import com.campusgo.store.InMemoryMerchantStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class MerchantServiceImpl implements MerchantService {
    private final InMemoryMerchantStore store;


    @Override
    public Merchant create(String username, String rawPassword, String name, String phone, String address, Double lat, Double lng, List<String> tags) {
        return store.create(username, rawPassword, name, phone, address, lat, lng, tags);
    }


    @Override public Optional<Merchant> findById(Long id) { return store.findById(id); }
    @Override public List<Merchant> search(String keyword) { return store.searchByKeyword(keyword); }
    @Override public List<Merchant> findAll() { return store.findAll(); }
    @Override public Merchant updateBasic(Long id, String phone, String address, List<String> tags) { return store.updateBasic(id, phone, address, tags); }
    @Override public Merchant updateStatus(Long id, MerchantStatus status) { return store.updateStatus(id, status); }
    @Override public boolean delete(Long id) { return store.delete(id); }
}
