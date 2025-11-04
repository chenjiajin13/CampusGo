package com.campusgo.dto;


import lombok.*;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RefundRequest {
    private Long amountCents;
    private String reason;
}
