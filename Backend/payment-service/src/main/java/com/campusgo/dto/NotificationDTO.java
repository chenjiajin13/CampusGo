package com.campusgo.dto;


import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationStatus;
import com.campusgo.enums.NotificationTargetType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationDTO {
    private Long id;
    private NotificationTargetType targetType;
    private Long targetId;
    private NotificationChannel channel;
    private String title;
    private String content;
    private Map<String, Object> data;
    private NotificationStatus status;
    private Instant createdAt;
    private Instant sentAt;
}