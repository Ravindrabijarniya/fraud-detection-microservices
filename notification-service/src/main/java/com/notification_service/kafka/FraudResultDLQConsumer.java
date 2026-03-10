package com.notification_service.kafka;

import com.common.events.FraudResultEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class FraudResultDLQConsumer {

    @KafkaListener(
            topics = "fraud-results-dlt",
            groupId = "notification-dlt-group"
    )
    public void consumeDLQ(FraudResultEvent event) {

        log.error("🚨 DLQ EVENT RECEIVED 🚨");
        log.error("User: {}", event.getUserEmail());
        log.error("TransactionId: {}", event.getTransactionId());
        log.error("Reason: {}", event.getReason());
        log.error("Fraud: {}", event.isFraud());

        // 🔔 Future:
        // - send email to ops
        // - push to Slack
        // - save to dlq_audit table
    }
}
