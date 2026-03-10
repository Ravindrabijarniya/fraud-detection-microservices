package com.authservice.repository;

import com.authservice.model.RefreshToken;
import com.authservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

//    Optional<RefreshToken> findByUser(User user);

}
