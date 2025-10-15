package com.campusgo.domain;


import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.VehicleType;
import lombok.*;


import java.time.Instant;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Runner {
    private Long id;
    private String username;
    private String passwordHash; // BCrypt hash
    private String phone;


    private VehicleType vehicleType;
    private RunnerStatus status;


    // simple geo
    private Double latitude; // nullable when unknown
    private Double longitude; // nullable when unknown


    private Double rating; // 0~5, nullable
    private Integer completedOrders;
    private Long totalEarningsCents;


    private Instant createdAt;
    private Instant updatedAt;
}