package com.campusgo.service;

import com.campusgo.domain.MenuItem;

import java.util.List;

public interface MenuItemService {
    List<MenuItem> listPublicMenu(Long merchantId);

    List<MenuItem> listAllByMerchant(Long merchantId);

    MenuItem create(Long merchantId, String name, Long priceCents, Boolean enabled);

    MenuItem update(Long merchantId, Long itemId, String name, Long priceCents, Boolean enabled);

    boolean delete(Long merchantId, Long itemId);
}

