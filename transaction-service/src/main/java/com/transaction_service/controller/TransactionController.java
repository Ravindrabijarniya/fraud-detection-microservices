package com.transaction_service.controller;

import com.transaction_service.model.Transaction;
import com.transaction_service.security.SecurityUtils;
import com.transaction_service.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService service;

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @PostMapping
    public Transaction create(@RequestBody Transaction tx) {

        // Extract user from JWT (NO custom header needed)
        String userId = SecurityUtils.getCurrentUserId();
        String email = SecurityUtils.getEmail();

        tx.setUserId(userId);
        tx.setUserEmail(email);

        return service.create(tx);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping("/{id}")
    public Transaction getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_USER')")
    @GetMapping
    public List<Transaction> getMyTransactions() {
        return service.getMyTransactions();
    }

}
