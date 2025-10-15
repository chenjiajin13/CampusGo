package com.campusgo.domain;


import com.campusgo.enums.*;
import lombok.*;


import java.time.Instant;
import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification {
    private Long id;
    private NotificationTargetType targetType; // USER/MERCHANT/RUNNER/ADMIN
    private Long targetId; // 接收方 ID


    private NotificationChannel channel; // EMAIL/SMS/PUSH（mock）
    private String title;
    private String content;
    private Map<String, Object> data; //


    private NotificationStatus status; // PENDING/SENT/FAILED
    private Instant createdAt;
    private Instant sentAt;
}