package com.campusgo.dto;


import com.campusgo.enums.*;
import lombok.*;


import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationCreateRequest {
    private NotificationTargetType targetType;
    private Long targetId;
    private NotificationChannel channel; // 可空：不传则默认 PUSH
    private String title;
    private String content;
    private Map<String, Object> data;
}