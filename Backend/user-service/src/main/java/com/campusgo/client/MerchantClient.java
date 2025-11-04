package com.campusgo.client;


import com.campusgo.DTO.MerchantDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "merchant-service", path = "/internal/merchants")
public interface MerchantClient {
    @GetMapping("/{id}")
    MerchantDTO findById(@PathVariable("id") Long id);
}
