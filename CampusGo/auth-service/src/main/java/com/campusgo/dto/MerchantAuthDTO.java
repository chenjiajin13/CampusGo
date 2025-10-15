package com.campusgo.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantAuthDTO {
    private Long id;
    private String username;
    private String passwordHash;
    private String role; // ROLE_MERCHANT
}