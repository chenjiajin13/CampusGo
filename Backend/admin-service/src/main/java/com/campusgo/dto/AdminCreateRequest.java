package com.campusgo.dto;


import com.campusgo.enums.AdminRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;




@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminCreateRequest {
    private String username;
    private String password;
    private String email;
    private String phone;
    private AdminRole role;
}
