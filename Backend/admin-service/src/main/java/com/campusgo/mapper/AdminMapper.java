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

    Admin findById(@Param("id") Long id);

    Admin findByUsername(@Param("username") String username);

    List<Admin> findAll();

    int updateBasic(@Param("id") Long id,
                    @Param("email") String email,
                    @Param("phone") String phone);

    int updateStatus(@Param("id") Long id, @Param("enabled") Boolean enabled);

    int updatePassword(@Param("id") Long id, @Param("passwordHash") String passwordHash);

    int deleteById(@Param("id") Long id);
}
