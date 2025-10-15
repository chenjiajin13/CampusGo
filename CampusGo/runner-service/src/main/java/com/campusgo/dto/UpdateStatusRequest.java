package com.campusgo.dto;


import com.campusgo.enums.RunnerStatus;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {
    private RunnerStatus status;
}
