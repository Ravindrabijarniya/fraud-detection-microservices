package com.notification_service.repository;

import com.notification_service.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByUserEmail(String userEmail);

    List<Notification> findByUserEmailOrderByCreatedAtDesc(String email);

    boolean existsByTransactionId(Long transactionId);
}
