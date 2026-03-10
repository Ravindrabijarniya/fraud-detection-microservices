package com.transaction_service.kafka;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {

    private Long transactionId;
    private String userEmail;
    private Double amount;
    private String location;

}
