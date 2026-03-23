package com.campusgo.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
public class OrderDetail {
    private Long orderId;
    private Long userId;
    private Long merchantId;
    private Long runnerId;
    private UserDTO user;
    private String customerName;
    private String customerPhone;
    private String customerAddress;
    private String status;
    private Long amountCents;
    private String paymentStatus;
    private Boolean runnerCanComplete;
    private List<OrderItemDetailDTO> items;

    public OrderDetail(Long orderId, UserDTO user, String status, Long amountCents, String paymentStatus) {
        this.orderId = orderId;
        this.user = user;
        this.status = status;
        this.amountCents = amountCents;
        this.paymentStatus = paymentStatus;
    }
}

