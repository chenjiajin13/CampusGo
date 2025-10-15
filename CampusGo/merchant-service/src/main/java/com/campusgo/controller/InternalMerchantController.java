package com.campusgo.controller;


import com.campusgo.dto.MerchantAuthDTO;
import com.campusgo.dto.MerchantDTO;
import com.campusgo.dto.UpdateStatusRequest;
import com.campusgo.mapper.MerchantMapper;
import com.campusgo.service.InternalMerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/merchants")
@RequiredArgsConstructor
public class InternalMerchantController {


    private final InternalMerchantService internalService;


    // for auth-service
    @GetMapping("/by-username/{username}")
    public ResponseEntity<MerchantAuthDTO> findByUsername(@PathVariable("username") String username) {
        return internalService.findByUsername(username)
                .map(MerchantMapper::toAuthDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // for order-service
    @GetMapping("/{id}")
    public ResponseEntity<MerchantDTO> getById(@PathVariable("id") Long id) {
        return internalService.findById(id)
                .map(MerchantMapper::toDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Operations/order flow: update business status
    @PatchMapping("/{id}/status")
    public ResponseEntity<MerchantDTO> updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
        return ResponseEntity.ok(MerchantMapper.toDTO(internalService.updateStatus(id, req.getStatus())));
    }
}
