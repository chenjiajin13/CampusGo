package com.campusgo.mapper;

import com.campusgo.domain.OrderPayment;
import com.campusgo.enums.OrderPaymentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderPaymentMapper {
    OrderPayment findByOrderId(@Param("orderId") Long orderId);
    OrderPayment findByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);
    int insert(OrderPayment payment);
    int updateStatus(@Param("id") Long id, @Param("status") OrderPaymentStatus status);
}
