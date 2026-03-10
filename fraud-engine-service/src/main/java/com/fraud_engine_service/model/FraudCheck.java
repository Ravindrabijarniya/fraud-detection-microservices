package com.fraud_engine_service.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class FraudCheck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long transactionId;
    private String userEmail;
    private Double amount;
    private String location;

    @Enumerated(EnumType.STRING)
    private FraudStatus status;

    private String reason;
    private LocalDateTime checkedAt;
}
