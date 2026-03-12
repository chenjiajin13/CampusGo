package com.campusgo.mapper;

import com.campusgo.domain.OrderSettlement;
import com.campusgo.enums.OrderSettlementStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface OrderSettlementMapper {
    OrderSettlement findByOrderId(@Param("orderId") Long orderId);
    OrderSettlement findByIdempotencyKey(@Param("idempotencyKey") String idempotencyKey);
    int insert(OrderSettlement settlement);
    int updateStatus(@Param("id") Long id, @Param("status") OrderSettlementStatus status);
}
