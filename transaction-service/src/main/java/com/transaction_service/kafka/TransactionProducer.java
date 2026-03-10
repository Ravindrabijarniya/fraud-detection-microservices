package com.transaction_service.kafka;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import com.common.events.TransactionEvent;


@Service
@RequiredArgsConstructor
public class TransactionProducer {

    private final KafkaTemplate<String, TransactionEvent> kafkaTemplate;

    @Value("${kafka.topic.transaction-created}")
    private String topic;

    public void sendTransaction(TransactionEvent event) {
        // ✅ USE CONFIGURED TOPIC, NOT HARDCODED STRING
        kafkaTemplate.send(topic, event);
    }
}
