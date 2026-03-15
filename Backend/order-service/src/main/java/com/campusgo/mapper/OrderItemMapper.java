package com.campusgo.mapper;

import com.campusgo.domain.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface OrderItemMapper {
    int insertBatch(@Param("items") List<OrderItem> items);
}
