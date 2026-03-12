package com.campusgo.mapper;

import com.campusgo.domain.CartItemRow;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface CartItemMapper {

    List<CartItemRow> listByUser(@Param("userId") Long userId);

    int countByUserAndMerchantNot(@Param("userId") Long userId, @Param("merchantId") Long merchantId);

    int upsertAdd(@Param("userId") Long userId,
                  @Param("merchantId") Long merchantId,
                  @Param("menuItemId") Long menuItemId,
                  @Param("quantity") Integer quantity,
                  @Param("unitPriceCents") Long unitPriceCents);

    int deleteItem(@Param("userId") Long userId, @Param("menuItemId") Long menuItemId);

    int deleteByUser(@Param("userId") Long userId);
}
