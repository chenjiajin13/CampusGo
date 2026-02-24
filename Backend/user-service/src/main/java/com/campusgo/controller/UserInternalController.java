package com.campusgo.controller;

import com.campusgo.DTO.RegisterRequest;
import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.DTO.UserDTO;
import com.campusgo.service.InternalUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
@Validated
public class UserInternalController {

    private final InternalUserService internalUserService;


    @GetMapping("/by-username/{username}")
    public ResponseEntity<UserAuthDTO> findByUsername(@PathVariable("username") String username) {
        UserAuthDTO dto = internalUserService.findByUsername(username);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }


    @PostMapping("/register")
    public ResponseEntity<UserAuthDTO> register(@RequestBody RegisterRequest req) {
        UserAuthDTO dto = internalUserService.register(req);
        return dto == null ? ResponseEntity.status(409).build() : ResponseEntity.ok(dto);
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> findUserDTOById(@PathVariable("id") Long id) {
        UserDTO dto = internalUserService.findUserDTOById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }
}
