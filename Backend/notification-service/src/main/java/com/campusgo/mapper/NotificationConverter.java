package com.campusgo.mapper;


import com.campusgo.domain.Notification;
import com.campusgo.dto.NotificationDTO;


public class NotificationConverter {
    public static NotificationDTO toDTO(Notification n) {
        if (n == null) return null;
        return NotificationDTO.builder()
                .id(n.getId())
                .targetType(n.getTargetType())
                .targetId(n.getTargetId())
                .channel(n.getChannel())
                .title(n.getTitle())
                .content(n.getContent())
                .data(n.getData())
                .status(n.getStatus())
                .createdAt(n.getCreatedAt())
                .sentAt(n.getSentAt())
                .build();
    }
}