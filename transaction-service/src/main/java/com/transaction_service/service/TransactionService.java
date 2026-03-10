package com.transaction_service.service;

//import com.transaction_service.kafka.TransactionEvent;
import com.common.events.TransactionEvent;
import com.transaction_service.kafka.TransactionProducer;
import com.transaction_service.model.Transaction;
import com.transaction_service.repository.TransactionRepository;
import com.transaction_service.security.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionProducer producer;
    private final TransactionRepository repository;

    public Transaction create(Transaction tx){
        tx.setStatus("CREATED");
        Transaction saved=repository.save(tx);

        producer.sendTransaction(
                new TransactionEvent(
                        saved.getId(),
                        saved.getUserEmail(),
                        saved.getAmount(),
                        saved.getLocation()
                )
        );

        return saved;
    }


    public Transaction getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
    }

    public List<Transaction> getMyTransactions() {
        String email = SecurityUtils.getEmail();
        return repository.findByUserEmail(email);
    }

}
