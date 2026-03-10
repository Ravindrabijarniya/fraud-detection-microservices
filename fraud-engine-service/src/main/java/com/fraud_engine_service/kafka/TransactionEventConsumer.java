package com.fraud_engine_service.kafka;

import com.common.events.TransactionEvent;
import com.fraud_engine_service.model.FraudCheck;
import com.fraud_engine_service.service.FraudDetectionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.DltStrategy;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TransactionEventConsumer {

    private final FraudDetectionService fraudService;
    private final FraudResultProducer producer;

    @RetryableTopic(
            attempts = "3",
            backoff = @org.springframework.retry.annotation.Backoff(
                    delay = 2000,
                    multiplier = 2
            ),
            topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
            dltStrategy = DltStrategy.FAIL_ON_ERROR
    )
    @KafkaListener(
            topics = "${kafka.topic.transaction-events}",
            groupId = "fraud-engine-group"
    )
    public void consume(TransactionEvent event) {

        log.info("📥 Received TransactionEvent {}", event);

        if (event.getAmount() > 1_000_000) {
            throw new RuntimeException("Fraud engine failed to process large transaction");
        }

        FraudCheck result = fraudService.check(
                event.getTransactionId(),
                event.getUserEmail(),
                event.getAmount(),
                event.getLocation()
        );



        producer.sendFraudResult(result);
    }
}
