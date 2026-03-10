package com.transaction_service.kafka;

import com.common.events.FraudResultEvent;
import com.transaction_service.model.Transaction;
import com.transaction_service.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class FraudResultConsumer {

    private final TransactionRepository repository;

    @KafkaListener(
            topics = "${kafka.topic.fraud-results}",
            groupId = "transaction-service-group"
    )
    public void consume(FraudResultEvent event) {

        log.info("📥 Received FraudResultEvent {}", event);

        Transaction tx = repository.findById(event.getTransactionId())
                .orElseThrow();

        if (event.isFraud()) {
            tx.setStatus("BLOCKED");
        } else {
            tx.setStatus("APPROVED");
        }

        repository.save(tx);

        log.info("Transaction {} updated to {}",
                tx.getId(), tx.getStatus());
    }
}
