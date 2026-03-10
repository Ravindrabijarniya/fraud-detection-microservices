package com.authservice.service;

import com.authservice.dto.LoginRequest;
import com.authservice.dto.LoginResponse;
import com.authservice.dto.SignupRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface AuthService {
    LoginResponse login(LoginRequest request, HttpServletResponse response);

    void signup(SignupRequest request);

    LoginResponse refreshToken(String refreshToken);

    void logout(String email, HttpServletResponse response);
}
