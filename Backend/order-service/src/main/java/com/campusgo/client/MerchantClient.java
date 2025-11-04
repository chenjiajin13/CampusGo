package com.campusgo.client;


import com.campusgo.dto.MerchantDTO;
import com.campusgo.dto.UpdateStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "merchant-service", path = "/internal/merchants")
public interface MerchantClient {
    @GetMapping("/{id}")
    MerchantDTO findById(@PathVariable("id") Long id);
    @PatchMapping("/{id}/status") MerchantDTO updateStatus(@PathVariable("id") Long id,
                                                           @RequestBody UpdateStatusRequest req);
}


