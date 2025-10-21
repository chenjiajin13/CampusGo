package com.campusgo.mapper;

import com.campusgo.domain.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface OrderMapper {

    int insert(Order o);                               // useGeneratedKeys=true

    Optional<Order> findById(@Param("id") Long id);

    List<Order> listAll();

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status);

    // 可选：指派骑手
    int updateRunner(@Param("id") Long id,
                     @Param("runnerId") Long runnerId);
}
