package com.campusgo.mapper;

import com.campusgo.domain.MenuItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MenuItemMapper {
    int insert(MenuItem item);

    Optional<MenuItem> findById(@Param("id") Long id);

    List<MenuItem> findByMerchantId(@Param("merchantId") Long merchantId);

    List<MenuItem> findEnabledByMerchantId(@Param("merchantId") Long merchantId);

    int update(@Param("id") Long id,
               @Param("merchantId") Long merchantId,
               @Param("name") String name,
               @Param("priceCents") Long priceCents,
               @Param("enabled") Boolean enabled);

    int deleteById(@Param("id") Long id, @Param("merchantId") Long merchantId);
}

