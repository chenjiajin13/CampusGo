package com.campusgo.dto;


import com.campusgo.enums.*;
import lombok.*;


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