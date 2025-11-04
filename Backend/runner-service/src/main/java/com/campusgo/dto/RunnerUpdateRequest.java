package com.campusgo.dto;


import com.campusgo.enums.VehicleType;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunnerUpdateRequest {
    private String phone;
    private VehicleType vehicleType;
}
