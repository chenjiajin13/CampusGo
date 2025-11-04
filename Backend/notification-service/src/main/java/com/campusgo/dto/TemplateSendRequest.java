package com.campusgo.dto;


import com.campusgo.enums.*;
import lombok.*;


import java.util.Map;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateSendRequest {
    private TemplateKey template;
    private NotificationTargetType targetType;
    private Long targetId;
    private Map<String, Object> params;
    private NotificationChannel channel;
}