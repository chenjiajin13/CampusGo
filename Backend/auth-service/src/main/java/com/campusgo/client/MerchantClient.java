package com.campusgo.client;

import com.campusgo.dto.MerchantAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "merchant-service", path = "/internal/merchants")
public interface MerchantClient {
    @GetMapping("/by-username/{username}")
    MerchantAuthDTO findByUsername(@PathVariable("username") String username);
}
