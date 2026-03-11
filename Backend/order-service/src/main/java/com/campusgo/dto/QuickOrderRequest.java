package com.campusgo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuickOrderRequest {
    private Long merchantId;
    private String address;
    private Boolean autoPay;
    private List<CartItemRequest> items;
}

