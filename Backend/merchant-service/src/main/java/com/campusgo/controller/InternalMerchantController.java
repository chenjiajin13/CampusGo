package com.campusgo.controller;


import com.campusgo.dto.MerchantAuthDTO;
import com.campusgo.dto.MerchantDTO;
import com.campusgo.dto.MenuItemDTO;
import com.campusgo.dto.UpdateStatusRequest;
import com.campusgo.mapper.MenuItemConverter;
import com.campusgo.mapper.MerchantConverter;
import com.campusgo.service.InternalMerchantService;
import com.campusgo.service.MenuItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/internal/merchants")
@RequiredArgsConstructor
public class InternalMerchantController {


    private final InternalMerchantService internalService;
    private final MenuItemService menuItemService;


    // for auth-service
    @GetMapping("/by-username/{username}")
    public ResponseEntity<MerchantAuthDTO> findByUsername(@PathVariable("username") String username) {
        return internalService.findByUsername(username)
                .map(MerchantConverter::toAuthDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // for order-service
    @GetMapping("/{id}")
    public ResponseEntity<MerchantDTO> getById(@PathVariable("id") Long id) {
        return internalService.findById(id)
                .map(MerchantConverter::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Operations/order flow: update business status
    @PatchMapping("/{id}/status")
    public ResponseEntity<MerchantDTO> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
        return ResponseEntity.ok(MerchantConverter.toDTO(internalService.updateStatus(id, req.getStatus())));
    }

    @GetMapping("/{id}/menu")
    public List<MenuItemDTO> getMenu(@PathVariable("id") Long merchantId) {
        return menuItemService.listPublicMenu(merchantId)
                .stream()
                .map(MenuItemConverter::toDTO)
                .collect(Collectors.toList());
    }
}
