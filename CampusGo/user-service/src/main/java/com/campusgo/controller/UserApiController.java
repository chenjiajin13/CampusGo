package com.campusgo.controller;

import com.campusgo.DTO.UserDTO;
import com.campusgo.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserApiController {

    private final UserService userService;
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getById(@PathVariable("id") Long id) {
        UserDTO dto = userService.findById(id);
        return dto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(dto);
    }

}
