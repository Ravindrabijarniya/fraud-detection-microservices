package com.notification_service.kafka;

import com.common.events.FraudResultEvent;
import com.notification_service.model.Notification;
import com.notification_service.model.NotificationType;
import com.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudResultConsumer {

    private final NotificationRepository repository;

    @RetryableTopic(
            attempts = "4",
            backoff = @Backoff(delay = 3000),
            dltTopicSuffix = "-dlt",
            retryTopicSuffix = "-retry"
    )
    @KafkaListener(
            topics = "${kafka.topic.fraud-results}",
            groupId = "notification-service-group"
    )
    public void consume(FraudResultEvent event) {

        String message;

        if (event.isFraud()) {
            message = "❌ Transaction BLOCKED: " + event.getReason();
        } else {
            message = "✅ Transaction APPROVED";
        }

        Notification notification = Notification.builder()
                .userEmail(event.getUserEmail())
                .transactionId(event.getTransactionId())
                .type(event.isFraud()
                        ? NotificationType.FRAUD_ALERT
                        : NotificationType.TRANSACTION_APPROVED)
                .message(message)
                .delivered(true)
                .createdAt(LocalDateTime.now())
                .build();

        repository.save(notification);

        log.info("📨 Notification saved for user {}", event.getUserEmail());
    }
}
