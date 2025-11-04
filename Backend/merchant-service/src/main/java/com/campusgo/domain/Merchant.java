package com.campusgo.domain;


import com.campusgo.enums.MerchantStatus;
import lombok.*;


import java.time.Instant;
import java.util.List;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Merchant {
    private Long id;
    private String username; // login username
    private String passwordHash; // BCrypt
    private String name; // store name
    private String phone;
    private String address;
    private MerchantStatus status; // OPEN/CLOSED/PAUSED
    private Double latitude;
    private Double longitude;
    private Double rating; // 0~5
    private Integer finishedOrders;
    private List<String> tags; // eg， "chicken","set meal"
    private Instant createdAt;
    private Instant updatedAt;
}
