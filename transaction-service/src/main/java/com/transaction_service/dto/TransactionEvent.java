package com.transaction_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionEvent {

    private String transactionId;
    private String userEmail;
    private Double amount;
    private String location;
}
