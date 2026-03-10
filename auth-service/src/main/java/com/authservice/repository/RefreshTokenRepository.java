package com.authservice.repository;

import com.authservice.model.RefreshToken;
import com.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(User user);

//    void deleteByUserId(Long userId);
    void deleteByUser(User user);
}
