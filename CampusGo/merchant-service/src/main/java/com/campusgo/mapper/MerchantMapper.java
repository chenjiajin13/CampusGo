package com.campusgo.mapper;

import com.campusgo.domain.Merchant;
import com.campusgo.enums.MerchantStatus;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface MerchantMapper {

    int insert(Merchant m); // useGeneratedKeys=true

    Optional<Merchant> findById(@Param("id") Long id);

    Optional<Merchant> findByUsername(@Param("username") String username);

    List<Merchant> findAll();

    List<Merchant> search(@Param("kw") String keyword);

    int updateBasic(@Param("id") Long id,
                    @Param("phone") String phone,
                    @Param("address") String address,
                    @Param("tags") List<String> tags);

    int updateStatus(@Param("id") Long id,
                     @Param("status") MerchantStatus status);

    int deleteById(@Param("id") Long id);
}
