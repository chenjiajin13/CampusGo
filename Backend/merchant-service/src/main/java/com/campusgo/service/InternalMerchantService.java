package com.campusgo.service;

import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;


import java.util.Optional;


/** Internal service（auth/order） */
public interface InternalMerchantService {
    Optional<Merchant> findByUsername(String username); //  auth-service
    Optional<Merchant> findById(Long id); //  order-service
    Merchant updateStatus(Long id, MerchantStatus status); // To operations/order flow
}
