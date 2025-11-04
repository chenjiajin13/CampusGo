package com.campusgo.dto;


import com.campusgo.enums.MerchantStatus;
import lombok.*;


import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MerchantDTO {
    private Long id;
    private String name;
    private String phone;
    private String address;
    private MerchantStatus status;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Integer finishedOrders;
    private List<String> tags;
}
