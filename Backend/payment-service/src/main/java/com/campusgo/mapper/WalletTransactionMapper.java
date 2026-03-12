package com.campusgo.mapper;

import com.campusgo.domain.WalletTransaction;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface WalletTransactionMapper {
    int insert(WalletTransaction tx);
    List<WalletTransaction> listByAccountId(@Param("accountId") Long accountId, @Param("limit") Integer limit);
}
