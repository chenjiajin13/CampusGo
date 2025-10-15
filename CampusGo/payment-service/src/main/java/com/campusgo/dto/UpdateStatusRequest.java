package com.campusgo.dto;


import com.campusgo.enums.PaymentStatus;
import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateStatusRequest {
    private PaymentStatus status; // SUCCESS/FAILED/REFUNDED/PENDING
}
