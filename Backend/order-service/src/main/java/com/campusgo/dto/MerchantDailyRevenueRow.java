package com.campusgo.dto;

import lombok.Data;

@Data
public class MerchantDailyRevenueRow {
    private String dayKey;
    private Long amountCents;
}
