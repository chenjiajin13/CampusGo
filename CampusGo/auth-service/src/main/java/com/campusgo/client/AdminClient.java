package com.campusgo.client;

import com.campusgo.dto.AdminAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "admin-service", path = "/internal/admins")
public interface AdminClient {
    @GetMapping("/by-username/{username}")
    AdminAuthDTO findByUsername(@PathVariable("username") String username);
}
