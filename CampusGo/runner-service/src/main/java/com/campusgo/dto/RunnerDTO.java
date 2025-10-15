package com.campusgo.dto;


import com.campusgo.enums.RunnerStatus;
import com.campusgo.enums.VehicleType;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunnerDTO {
    private Long id;
    private String username;
    private String phone;
    private VehicleType vehicleType;
    private RunnerStatus status;
    private Double latitude;
    private Double longitude;
    private Double rating;
    private Integer completedOrders;
    private Long totalEarningsCents;
}
