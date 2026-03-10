package com.fraud_engine_service.kafka;

import com.common.events.TransactionEvent;
import com.fraud_engine_service.model.FraudCheck;
import com.fraud_engine_service.model.FraudStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FraudTransactionDLQConsumer {

    private final FraudResultProducer producer;

    @KafkaListener(
            topics = "transaction.created-dlt",
            groupId = "fraud-engine-dlt-group"
    )
    public void consumeDLQ(TransactionEvent event) {

        log.error("🚨 FRAUD ENGINE DLQ EVENT 🚨 {}", event);

        // Convert DLQ failure into fraud result
        FraudCheck failed = FraudCheck.builder()
                .transactionId(event.getTransactionId())
                .userEmail(event.getUserEmail())
                .status(FraudStatus.FRAUD)
                .reason("Fraud Engine DLQ Failure")
                .build();

        producer.sendFraudResult(failed);
    }
}
