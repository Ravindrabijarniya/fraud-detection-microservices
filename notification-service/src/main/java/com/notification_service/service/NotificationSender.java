package com.notification_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationSender {

    public void sendEmail(String to, String message) {
        log.info("📧 Sending EMAIL to {}", to);
        log.info("📨 Message: {}", message);
    }

    public void sendSms(String to, String message) {
        log.info("📱 Sending SMS to {}", to);
        log.info("📨 Message: {}", message);
    }

    // Future:
    // sendPush()
    // sendWhatsApp()
}
