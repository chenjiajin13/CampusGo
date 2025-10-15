package com.campusgo.controller;


import com.campusgo.domain.Runner;
import com.campusgo.dto.*;
import com.campusgo.enums.RunnerStatus;
import com.campusgo.mapper.RunnerMapper;
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
        return RunnerMapper.toPublicDTO(service.create(req.getUsername(), req.getPassword(), req.getPhone(), req.getVehicleType()));
    }


    @GetMapping("/{id}")
    public ResponseEntity<RunnerDTO> get(@PathVariable("id") Long id) {
        return service.findById(id).map(RunnerMapper::toPublicDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<RunnerDTO> list(@RequestParam(value = "status", required = false) RunnerStatus status) {
        List<Runner> list = (status == null) ? service.findAll() : service.findByStatus(status);
        return list.stream().map(RunnerMapper::toPublicDTO).collect(Collectors.toList());
    }


    @PutMapping("/{id}")
    public RunnerDTO updateBasic(@PathVariable Long id, @RequestBody RunnerUpdateRequest req) {
        return RunnerMapper.toPublicDTO(service.updateBasic(id, req.getPhone(), req.getVehicleType()));
    }


    @PatchMapping("/{id}/location")
    public RunnerDTO updateLocation(@PathVariable Long id, @RequestBody UpdateLocationRequest req) {
        return RunnerMapper.toPublicDTO(service.updateLocation(id, req.getLatitude(), req.getLongitude()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boolean removed = service.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
