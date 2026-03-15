package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MerchantItemShareDTO {
    private String itemName;
    private Long quantity;
    private Long amountCents;
}
