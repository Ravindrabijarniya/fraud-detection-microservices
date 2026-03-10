package com.common.events;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FraudResultEvent {

    private Long transactionId;
    private String userEmail;
    private boolean fraud;
    private String reason;
}
