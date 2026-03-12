package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BatchCheckoutResponse {
    private Integer orderCount;
    private Long totalAmountCents;
    private Boolean allPaid;
    private List<BatchCheckoutItemDTO> orders;
}
