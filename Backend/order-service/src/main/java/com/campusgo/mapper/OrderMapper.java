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

    List<Order> listByUserId(@Param("userId") Long userId);

    List<Order> listByMerchantId(@Param("merchantId") Long merchantId);

    List<Order> listByRunnerId(@Param("runnerId") Long runnerId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") String status);

    int updateRunner(@Param("id") Long id,
                     @Param("runnerId") Long runnerId);
}
