package com.campusgo.dto;


import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateLocationRequest {
    private Double latitude;
    private Double longitude;
}