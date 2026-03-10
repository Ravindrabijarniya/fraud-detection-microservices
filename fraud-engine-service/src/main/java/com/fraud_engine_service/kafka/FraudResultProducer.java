package com.fraud_engine_service.kafka;

import com.common.events.FraudResultEvent;
import com.fraud_engine_service.model.FraudCheck;
import com.fraud_engine_service.model.FraudStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class FraudResultProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topic.fraud-results}")
    private String topic;

    public void sendFraudResult(FraudCheck check) {

        log.info("📤 Publishing fraud result for txId={}", check.getTransactionId());

        FraudResultEvent event = new FraudResultEvent(
                check.getTransactionId(),
                check.getUserEmail(),
                check.getStatus() == FraudStatus.FRAUD,
                check.getReason()
        );

        kafkaTemplate.send(topic, event);
    }
}
