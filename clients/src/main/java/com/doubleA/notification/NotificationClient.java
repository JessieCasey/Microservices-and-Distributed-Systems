package com.doubleA.notification;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("notification-service")
public interface NotificationClient {

    @PostMapping(path = "api/v1/notification")
    void sendNotification(NotificationRequest notificationRequest);

}
