package com.campusgo.dto;


import com.campusgo.enums.NotificationChannel;
import com.campusgo.enums.NotificationTargetType;
import com.campusgo.enums.TemplateKey;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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