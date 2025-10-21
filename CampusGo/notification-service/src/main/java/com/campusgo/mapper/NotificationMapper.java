package com.campusgo.mapper;

import com.campusgo.domain.Notification;
import com.campusgo.enums.NotificationStatus;
import com.campusgo.enums.NotificationTargetType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface NotificationMapper {

    int insert(Notification n); // useGeneratedKeys=true

    Optional<Notification> findById(@Param("id") Long id);

    List<Notification> listInbox(@Param("type") NotificationTargetType type,
                                 @Param("targetId") Long targetId);

    int markSent(@Param("id") Long id);

    int updateStatus(@Param("id") Long id,
                     @Param("status") NotificationStatus status);
}
