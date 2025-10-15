package com.campusgo.dto;


import lombok.*;


import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantCreateRequest {
    private String username;
    private String password;
    private String name;
    private String phone;
    private String address;
    private Double latitude;
    private Double longitude;
    private List<String> tags;
}
