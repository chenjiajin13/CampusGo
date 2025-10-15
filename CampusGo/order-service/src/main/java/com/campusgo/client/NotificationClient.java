package com.campusgo.client;

import com.campusgo.dto.NotificationDTO;
import com.campusgo.dto.TemplateSendRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", path = "/internal/notifications")
public interface NotificationClient {
    @PostMapping("/send-template")
    NotificationDTO sendTemplate(@RequestBody TemplateSendRequest req);
}
