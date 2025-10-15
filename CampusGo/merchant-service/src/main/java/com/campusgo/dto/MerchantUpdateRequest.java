package com.campusgo.dto;


import lombok.*;


import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantUpdateRequest {
    private String phone;
    private String address;
    private List<String> tags;
}
