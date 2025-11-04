package com.campusgo.dto;


import com.campusgo.enums.VehicleType;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RunnerCreateRequest {
    private String username;
    private String password; // plain text from client, will be encoded
    private String phone;
    private VehicleType vehicleType;
}
