package com.campusgo.mapper;

import com.campusgo.DTO.UserDTO;
import com.campusgo.DTO.UserAuthDTO;
import com.campusgo.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Component;

import java.util.List;


public interface UserMapper {
    User findById(@Param("id") Long id);
    List<User> findAll();
    int insert(User u);
    int update(User u);
    int deleteById(@Param("id") Long id);
    User findByUsername(@Param("username") String username);
}


