package com.notification_service.controller;

import com.notification_service.model.Notification;
import com.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/admin/notifications")
@RequiredArgsConstructor
public class NotificationAdminController {

    private final NotificationRepository repository;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping
    public List<Notification> all() {
        return repository.findAll();
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/{email}")
    public List<Notification> byUser(@PathVariable String email) {
        return repository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/{email}/simple")
    public List<Notification> byUserSimple(@PathVariable String email) {
        return repository.findByUserEmail(email);
    }
}
