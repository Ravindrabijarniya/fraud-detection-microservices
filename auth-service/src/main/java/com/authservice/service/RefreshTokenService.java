package com.authservice.service;

import com.authservice.model.RefreshToken;
import com.authservice.model.User;

public interface RefreshTokenService {

    RefreshToken createOrUpdateRefreshToken(User user);

    RefreshToken verifyExpiration(String token);

    void deleteByUser(User user);
}
