package com.campusgo.domain;

import lombok.*;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    private Long id;
    private Long userId;
    private Long merchantId;
    private Long runnerId;        // can be null
    private Long amountCents;
    private String status;        // CREATED / PAID / DELIVERED
    private Instant createdAt;
}
