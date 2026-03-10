package com.fraud_engine_service.service;

import com.fraud_engine_service.model.*;
import com.fraud_engine_service.repository.FraudCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class FraudDetectionService {

    private final FraudCheckRepository repository;

    public FraudCheck check(
            Long transactionId,
            String userEmail,
            Double amount,
            String location
    ) {
        FraudStatus status = FraudStatus.CLEAR;
        String reason = "OK";

        if (amount > 10000) {
            status = FraudStatus.FRAUD;
            reason = "High transaction amount";
        } else if ("UNKNOWN".equalsIgnoreCase(location)) {
            status = FraudStatus.SUSPICIOUS;
            reason = "Unknown location";
        }

        FraudCheck fraudCheck = FraudCheck.builder()
                .transactionId(transactionId)
                .userEmail(userEmail)
                .amount(amount)
                .location(location)
                .status(status)
                .reason(reason)
                .checkedAt(LocalDateTime.now())
                .build();

        return repository.save(fraudCheck);
    }
}
