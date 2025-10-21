package com.campusgo.mapper;

import com.campusgo.domain.Admin;
import com.campusgo.enums.AdminRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface AdminMapper {

    int insert(Admin a);                                  // useGeneratedKeys

    Optional<Admin> findById(@Param("id") Long id);

    Optional<Admin> findByUsername(@Param("username") String username);

    List<Admin> findAll();

    int updateStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);

    int deleteById(@Param("id") Long id);
}
