package com.fraud_engine_service.repository;

import com.fraud_engine_service.model.FraudCheck;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FraudCheckRepository extends JpaRepository<FraudCheck, Long> {
}
