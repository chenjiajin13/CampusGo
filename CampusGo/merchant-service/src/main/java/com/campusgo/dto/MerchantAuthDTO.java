package com.campusgo.dto;


import lombok.*;


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