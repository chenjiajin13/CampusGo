package com.campusgo.client;

import com.campusgo.dto.RunnerAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "runner-service", path = "/internal/runners")
public interface RunnerClient {

    @GetMapping("/by-username/{username}")
    RunnerAuthDTO findByUsername(@PathVariable("username")  String username);

}

