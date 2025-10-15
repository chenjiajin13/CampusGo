package com.campusgo.controller;


import com.campusgo.domain.Merchant;
import com.campusgo.dto.MerchantCreateRequest;
import com.campusgo.dto.MerchantDTO;
import com.campusgo.dto.MerchantUpdateRequest;
import com.campusgo.dto.UpdateStatusRequest;
import com.campusgo.enums.MerchantStatus;
import com.campusgo.mapper.MerchantMapper;
import com.campusgo.service.MerchantService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/merchants")
@RequiredArgsConstructor
public class PublicMerchantController {


    private final MerchantService service;


    @PostMapping
    public MerchantDTO create(@RequestBody MerchantCreateRequest req) {
        Merchant m = service.create(req.getUsername(), req.getPassword(), req.getName(), req.getPhone(), req.getAddress(), req.getLatitude(), req.getLongitude(), req.getTags());
        return MerchantMapper.toDTO(m);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MerchantDTO> get(@PathVariable("id") Long id) {
        return service.findById(id).map(MerchantMapper::toDTO).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }


    @GetMapping
    public List<MerchantDTO> list(@RequestParam(value = "q", required = false) String keyword) {
        List<Merchant> list = (keyword == null) ? service.findAll() : service.search(keyword);
        return list.stream().map(MerchantMapper::toDTO).collect(Collectors.toList());
    }


    @PutMapping("/{id}")
    public MerchantDTO updateBasic(@PathVariable("id") Long id, @RequestBody MerchantUpdateRequest req) {
        return MerchantMapper.toDTO(service.updateBasic(id, req.getPhone(), req.getAddress(), req.getTags()));
    }


    @PatchMapping("/{id}/status")
    public MerchantDTO updateStatus(@PathVariable("id") Long id, @RequestBody UpdateStatusRequest req) {
        return MerchantMapper.toDTO(service.updateStatus(id, req.getStatus()));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        return service.delete(id) ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }


    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<String> handleNotFound(NoSuchElementException e) {
        return ResponseEntity.notFound().build();
    }
}
