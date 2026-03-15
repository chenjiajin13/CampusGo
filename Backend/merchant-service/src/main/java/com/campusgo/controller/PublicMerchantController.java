package com.campusgo.controller;


import com.campusgo.domain.Merchant;
import com.campusgo.dto.MerchantCreateRequest;
import com.campusgo.dto.MerchantDTO;
import com.campusgo.dto.MerchantUpdateRequest;
import com.campusgo.dto.MenuItemDTO;
import com.campusgo.dto.MenuItemUpsertRequest;
import com.campusgo.dto.UpdateStatusRequest;
import com.campusgo.dto.UpdatePasswordRequest;
import com.campusgo.mapper.MenuItemConverter;
import com.campusgo.mapper.MerchantConverter;
import com.campusgo.service.MenuItemService;
import com.campusgo.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class PublicMerchantController {


    private final MerchantService service;
    private final MenuItemService menuItemService;


    @PostMapping
    public MerchantDTO create(@RequestBody MerchantCreateRequest req) {
        Merchant m = service.create(req.getUsername(), req.getPassword(), req.getName(), req.getPhone(), req.getAddress(), req.getLatitude(), req.getLongitude(), req.getTags());
        return MerchantConverter.toDTO(m);
    }


    @GetMapping("/{id:\\d+}")
    public ResponseEntity<MerchantDTO> get(@PathVariable("id") Long id) {
        return service.findById(id).map(MerchantConverter::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<MerchantDTO> me(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                          @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"MERCHANT".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        return service.findById(userId).map(MerchantConverter::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<MerchantDTO> list(@RequestParam(value = "q", required = false) String keyword) {
        List<Merchant> list = (keyword == null) ? service.findAll() : service.search(keyword);
        return list.stream().map(MerchantConverter::toDTO).collect(Collectors.toList());
    }


    @PutMapping("/{id:\\d+}")
    public MerchantDTO updateBasic(@PathVariable("id") Long id, @RequestBody MerchantUpdateRequest req) {
        return MerchantConverter.toDTO(service.updateBasic(id, req.getName(), req.getPhone(), req.getAddress(), req.getTags()));
    }

    @PutMapping("/me")
    public ResponseEntity<MerchantDTO> updateMe(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                @RequestBody MerchantUpdateRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"MERCHANT".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(MerchantConverter.toDTO(service.updateBasic(userId, req.getName(), req.getPhone(), req.getAddress(), req.getTags())));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                 @RequestBody UpdatePasswordRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"MERCHANT".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        service.updatePassword(userId, req.getNewPassword());
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id:\\d+}/status")
    public MerchantDTO updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
        return MerchantConverter.toDTO(service.updateStatus(id, req.getStatus()));
    }


    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id:\\d+}/menu")
    public List<MenuItemDTO> menu(@PathVariable("id") Long merchantId) {
        return menuItemService.listPublicMenu(merchantId)
                .stream()
                .map(MenuItemConverter::toDTO)
                .collect(Collectors.toList());
    }

    @PostMapping("/{id:\\d+}/menu")
    public MenuItemDTO addMenuItem(@PathVariable("id") Long merchantId,
                                   @RequestBody MenuItemUpsertRequest req) {
        return MenuItemConverter.toDTO(
                menuItemService.create(merchantId, req.getName(), req.getPriceCents(), req.getEnabled())
        );
    }

    @PutMapping("/{id:\\d+}/menu/{itemId:\\d+}")
    public MenuItemDTO updateMenuItem(@PathVariable("id") Long merchantId,
                                      @PathVariable("itemId") Long itemId,
                                      @RequestBody MenuItemUpsertRequest req) {
        return MenuItemConverter.toDTO(
                menuItemService.update(merchantId, itemId, req.getName(), req.getPriceCents(), req.getEnabled())
        );
    }

    @DeleteMapping("/{id:\\d+}/menu/{itemId:\\d+}")
    public ResponseEntity<Void> deleteMenuItem(@PathVariable("id") Long merchantId,
                                               @PathVariable("itemId") Long itemId) {
        return menuItemService.delete(merchantId, itemId)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
