package com.campusgo.service;


import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;


import java.util.List;
import java.util.Optional;


/** public */
public interface MerchantService {
    Merchant create(String username, String rawPassword, String name, String phone, String address,
                    Double lat, Double lng, java.util.List<String> tags);


    Optional<Merchant> findById(Long id);
    java.util.List<Merchant> search(String keyword);
    java.util.List<Merchant> findAll();


    Merchant updateBasic(Long id, String name, String phone, String address, java.util.List<String> tags);
    void updatePassword(Long id, String newPassword);
    Merchant updateStatus(Long id, MerchantStatus status);
    boolean delete(Long id);
}
