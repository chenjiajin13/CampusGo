package com.campusgo.mapper;

import com.campusgo.domain.Payment;
import com.campusgo.enums.PaymentMethod;
import com.campusgo.enums.PaymentStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface PaymentMapper {

    int insert(Payment p);                        // useGeneratedKeys=true

    Optional<Payment> findById(@Param("id") Long id);

    Optional<Payment> findByOrderId(@Param("orderId") Long orderId);

    List<Payment> listByUser(@Param("userId") Long userId);

    int updateStatus(@Param("id") Long id,
                     @Param("status") PaymentStatus status);

    int updateProviderTxnId(@Param("id") Long id,
                            @Param("providerTxnId") String providerTxnId);

    int deleteById(@Param("id") Long id);
}
