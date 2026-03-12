package com.campusgo.mapper;

import com.campusgo.domain.WalletAccount;
import com.campusgo.enums.WalletOwnerType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface WalletAccountMapper {
    WalletAccount findByOwner(@Param("ownerType") WalletOwnerType ownerType, @Param("ownerId") Long ownerId);
    WalletAccount findByOwnerForUpdate(@Param("ownerType") WalletOwnerType ownerType, @Param("ownerId") Long ownerId);
    int insert(WalletAccount account);
    int updateBalance(@Param("id") Long id, @Param("balanceCents") Long balanceCents);
}
