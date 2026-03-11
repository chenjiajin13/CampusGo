package com.campusgo.service.impl;

import com.campusgo.domain.MenuItem;
import com.campusgo.mapper.MenuItemMapper;
import com.campusgo.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class MenuItemServiceImpl implements MenuItemService {

    private final MenuItemMapper menuItemMapper;

    @Override
    public List<MenuItem> listPublicMenu(Long merchantId) {
        return menuItemMapper.findEnabledByMerchantId(merchantId);
    }

    @Override
    public List<MenuItem> listAllByMerchant(Long merchantId) {
        return menuItemMapper.findByMerchantId(merchantId);
    }

    @Override
    @Transactional
    public MenuItem create(Long merchantId, String name, Long priceCents, Boolean enabled) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("MENU_NAME_REQUIRED");
        }
        if (priceCents == null || priceCents < 0) {
            throw new IllegalArgumentException("MENU_PRICE_INVALID");
        }
        MenuItem item = MenuItem.builder()
                .merchantId(merchantId)
                .name(name.trim())
                .priceCents(priceCents)
                .enabled(enabled == null ? Boolean.TRUE : enabled)
                .createdAt(Instant.now())
                .updatedAt(Instant.now())
                .build();
        menuItemMapper.insert(item);
        return item;
    }

    @Override
    @Transactional
    public MenuItem update(Long merchantId, Long itemId, String name, Long priceCents, Boolean enabled) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("MENU_NAME_REQUIRED");
        }
        if (priceCents == null || priceCents < 0) {
            throw new IllegalArgumentException("MENU_PRICE_INVALID");
        }
        int affected = menuItemMapper.update(itemId, merchantId, name.trim(), priceCents, enabled == null ? Boolean.TRUE : enabled);
        if (affected <= 0) {
            throw new NoSuchElementException("MENU_ITEM_NOT_FOUND");
        }
        return menuItemMapper.findById(itemId).orElseThrow(() -> new NoSuchElementException("MENU_ITEM_NOT_FOUND"));
    }

    @Override
    @Transactional
    public boolean delete(Long merchantId, Long itemId) {
        return menuItemMapper.deleteById(itemId, merchantId) > 0;
    }
}

