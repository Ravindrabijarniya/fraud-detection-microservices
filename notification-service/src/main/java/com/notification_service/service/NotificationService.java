package com.notification_service.service;

import com.common.events.FraudResultEvent;
import com.notification_service.model.Notification;
import com.notification_service.model.NotificationType;
import com.notification_service.repository.NotificationRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository repository;
    private final NotificationSender sender;

    @Transactional
    public void handleFraudResult(FraudResultEvent event) {

        // 🔒 Idempotency check (VERY IMPORTANT)
        if (repository.existsByTransactionId(event.getTransactionId())) {
            return;
        }

        Notification notification = Notification.builder()
                .transactionId(event.getTransactionId())
                .userEmail(event.getUserEmail())
                .type(event.isFraud()
                        ? NotificationType.FRAUD_ALERT
                        : NotificationType.TRANSACTION_APPROVED)
                .message(event.isFraud()
                        ? "🚨 Transaction BLOCKED: " + event.getReason()
                        : "✅ Transaction APPROVED")
                .delivered(false)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);

        // 📤 Send notification
        sender.sendEmail(notification.getUserEmail(), notification.getMessage());

        // ✅ Mark delivered
        notification.setDelivered(true);
        repository.save(notification);
    }
}
