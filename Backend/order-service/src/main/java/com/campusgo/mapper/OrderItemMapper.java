package com.campusgo.mapper;

import com.campusgo.domain.OrderItem;
import com.campusgo.dto.OrderItemDetailDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    int insertBatch(@Param("items") List<OrderItem> items);
    List<OrderItemDetailDTO> listByOrderId(@Param("orderId") Long orderId);
}
