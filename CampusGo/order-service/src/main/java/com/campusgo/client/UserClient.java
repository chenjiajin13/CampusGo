package com.campusgo.client;


import com.campusgo.dto.UserDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient(name = "user-service",
//        url = "http://localhost:8080",
        path = "/internal/users"
)
public interface UserClient {

    @GetMapping("/{id}")
    UserDTO findById(@PathVariable("id") Long id); //

}

