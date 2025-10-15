package com.campusgo.mapper;


import com.campusgo.domain.Merchant;
import com.campusgo.dto.MerchantAuthDTO;
import com.campusgo.dto.MerchantDTO;


public class MerchantMapper {
    public static MerchantDTO toDTO(Merchant m) {
        if (m == null) return null;
        return MerchantDTO.builder()
                .id(m.getId())
                .name(m.getName())
                .phone(m.getPhone())
                .address(m.getAddress())
                .status(m.getStatus())
                .latitude(m.getLatitude())
                .longitude(m.getLongitude())
                .rating(m.getRating())
                .finishedOrders(m.getFinishedOrders())
                .tags(m.getTags())
                .build();
    }


    public static MerchantAuthDTO toAuthDTO(Merchant m) {
        if (m == null) return null;
        return MerchantAuthDTO.builder()
                .id(m.getId())
                .username(m.getUsername())
                .passwordHash(m.getPasswordHash())
                .role("ROLE_MERCHANT")
                .build();
    }
}
