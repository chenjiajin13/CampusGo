package com.campusgo.DTO;

import lombok.Data;

@Data
public class RegisterRequest {

        private String username;
        private String passwordHash;
        private String phone;
        private String email;
}
