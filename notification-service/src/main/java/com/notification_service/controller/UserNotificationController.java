package com.notification_service.controller;

import com.notification_service.model.Notification;
import com.notification_service.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class UserNotificationController {

    private final NotificationRepository repository;

    @PreAuthorize("hasAuthority('USER')")
    @GetMapping("/me")
    public List<Notification> myNotifications() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        String email = authentication.getName(); // ← JWT `sub`

        System.out.println("🔐 JWT EMAIL = [" + email + "]");

        return repository.findByUserEmailOrderByCreatedAtDesc(email);
    }
}
