package com.campusgo.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class MerchantAnalyticsDTO {
    private String weekStart;
    private String weekEnd;
    private Long selectedWeekRevenueCents;
    private Long lifetimeRevenueCents;
    private Long annualRevenueCents;
    private Long completedOrderCount;
    private List<MerchantDailyRevenueDTO> dailyRevenue;
    private List<MerchantItemShareDTO> itemShare;
}
