package com.fraud_engine_service.controller;

import com.fraud_engine_service.model.FraudCheck;
import com.fraud_engine_service.repository.FraudCheckRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/fraud")
@RequiredArgsConstructor
public class FraudAdminController {

    private final FraudCheckRepository repository;

    @GetMapping
    public List<FraudCheck> getAll() {
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public FraudCheck getById(@PathVariable Long id) {
        return repository.findById(id).orElseThrow();
    }
}
