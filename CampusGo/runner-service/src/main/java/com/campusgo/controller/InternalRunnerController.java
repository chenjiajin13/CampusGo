package com.campusgo.controller;

import com.campusgo.dto.RunnerAuthDTO;
import com.campusgo.dto.RunnerDTO;
import com.campusgo.dto.UpdateStatusRequest;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.mapper.RunnerMapper;
import com.campusgo.service.InternalRunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/internal/runners")
@RequiredArgsConstructor
public class InternalRunnerController {


    private final InternalRunnerService internalService;


    // for auth-service
    @GetMapping("/by-username/{username}")
    public ResponseEntity<RunnerAuthDTO> findByUsername(@PathVariable("username") String username) {
        return internalService.findByUsername(username)
                .map(RunnerMapper::toAuthDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // for order-service
    @GetMapping("/assign/any-available")
    public ResponseEntity<RunnerDTO> pickAnyAvailable() {
        return internalService.findAnyAvailable()
                .map(RunnerMapper::toPublicDTO)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }


    // for order-service
    @PatchMapping("/{id}/status")
    public ResponseEntity<RunnerDTO> updateStatus(@PathVariable("id")  Long id, @RequestBody UpdateStatusRequest req) {
        RunnerStatus status = req.getStatus();
        if (status == null) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok(RunnerMapper.toPublicDTO(internalService.updateStatus(id, status)));
    }
}
