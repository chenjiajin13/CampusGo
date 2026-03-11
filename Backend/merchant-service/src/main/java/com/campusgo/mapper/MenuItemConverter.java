package com.campusgo.mapper;

import com.campusgo.domain.MenuItem;
import com.campusgo.dto.MenuItemDTO;

public class MenuItemConverter {
    public static MenuItemDTO toDTO(MenuItem item) {
        if (item == null) {
            return null;
        }
        return MenuItemDTO.builder()
                .id(item.getId())
                .merchantId(item.getMerchantId())
                .name(item.getName())
                .priceCents(item.getPriceCents())
                .enabled(item.getEnabled())
                .build();
    }
}

