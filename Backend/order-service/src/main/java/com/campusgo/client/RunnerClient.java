package com.campusgo.client;


import com.campusgo.dto.RunnerDTO;
import com.campusgo.dto.UpdateStatusRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "runner-service",
//        url = "http://localhost:8080",
        path = "/internal/runners"
)
public interface RunnerClient {
    @GetMapping("/assign/any-available")
    RunnerDTO pickAnyAvailable();

    @PatchMapping("/{id}/status")
    RunnerDTO updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req);
}
