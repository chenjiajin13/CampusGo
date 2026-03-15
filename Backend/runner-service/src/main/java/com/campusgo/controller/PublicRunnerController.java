package com.campusgo.controller;


import com.campusgo.domain.Runner;
import com.campusgo.dto.*;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.mapper.RunnerConverter;
import com.campusgo.service.RunnerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


/** public*/
@RestController
@RequestMapping("/api/runners")
@RequiredArgsConstructor
public class PublicRunnerController {


    private final RunnerService service;


    @PostMapping
    public RunnerDTO create(@RequestBody RunnerCreateRequest req) {
        return RunnerConverter.toPublicDTO(service.create(req.getUsername(), req.getPassword(), req.getPhone(), req.getVehicleType()));
    }


    @GetMapping("/{id:\\d+}")
    public ResponseEntity<RunnerDTO> get(@PathVariable("id") Long id) {
        return service.findById(id).map(RunnerConverter::toPublicDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/me")
    public ResponseEntity<RunnerDTO> me(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                        @RequestHeader(value = "X-Principal-Type", required = false) String pt) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"RUNNER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        return service.findById(userId).map(RunnerConverter::toPublicDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<RunnerDTO> list(@RequestParam(value = "status", required = false) RunnerStatus status) {
        List<Runner> list = (status == null) ? service.findAll() : service.findByStatus(status);
        return list.stream().map(RunnerConverter::toPublicDTO).collect(Collectors.toList());
    }


    @PutMapping("/{id:\\d+}")
    public RunnerDTO updateBasic(@PathVariable Long id, @RequestBody RunnerUpdateRequest req) {
        return RunnerConverter.toPublicDTO(service.updateBasic(id, req.getPhone(), req.getVehicleType()));
    }

    @PutMapping("/me")
    public ResponseEntity<RunnerDTO> updateMe(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                              @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                              @RequestBody RunnerUpdateRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"RUNNER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(RunnerConverter.toPublicDTO(service.updateBasic(userId, req.getPhone(), req.getVehicleType())));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> updateMyPassword(@RequestHeader(value = "X-User-Id", required = false) Long userId,
                                                 @RequestHeader(value = "X-Principal-Type", required = false) String pt,
                                                 @RequestBody UpdatePasswordRequest req) {
        if (userId == null) {
            return ResponseEntity.status(401).build();
        }
        if (pt == null || !"RUNNER".equalsIgnoreCase(pt)) {
            return ResponseEntity.status(403).build();
        }
        service.updatePassword(userId, req.getNewPassword());
        return ResponseEntity.noContent().build();
    }


    @PatchMapping("/{id:\\d+}/location")
    public RunnerDTO updateLocation(@PathVariable Long id, @RequestBody UpdateLocationRequest req) {
        return RunnerConverter.toPublicDTO(service.updateLocation(id, req.getLatitude(), req.getLongitude()));
    }


    @DeleteMapping("/{id:\\d+}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = service.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
