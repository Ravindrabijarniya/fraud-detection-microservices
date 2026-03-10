package com.authservice.service;

import com.authservice.dto.LoginRequest;
import com.authservice.dto.LoginResponse;
import com.authservice.dto.SignupRequest;
import com.authservice.model.RefreshToken;
import com.authservice.model.Role;
import com.authservice.model.User;
import com.authservice.repository.UserRepository;
import com.authservice.security.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // ========================= LOGIN =========================
    @Override
    public LoginResponse login(LoginRequest request, HttpServletResponse response) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        String accessToken = jwtUtil.generateToken(
                user.getEmail(),
                Map.of(
                        "roles",
                        user.getRoles().stream().map(Enum::name).toList()
                )
        );

        RefreshToken refreshToken = refreshTokenService.createOrUpdateRefreshToken(user);

        Cookie cookie = new Cookie("refreshToken", refreshToken.getToken());
        cookie.setHttpOnly(true);
        cookie.setSecure(false); // true in production (HTTPS)
        cookie.setPath("/auth");
        cookie.setMaxAge(7 * 24 * 60 * 60); // 7 days

        response.addCookie(cookie);

        return new LoginResponse(accessToken);
    }

    // ========================= SIGNUP =========================
    @Override
    public void signup(SignupRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already registered");
        }

        User user = new User();
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(Role.USER));

        userRepository.save(user);
    }

    // ========================= REFRESH TOKEN =========================
    @Override
    public LoginResponse refreshToken(String refreshToken) {

        RefreshToken verifiedToken =
                refreshTokenService.verifyExpiration(refreshToken);

        User user = verifiedToken.getUser();

        String newAccessToken = jwtUtil.generateToken(
                user.getEmail(),
                Map.of(
                        "roles",
                        user.getRoles().stream().map(Enum::name).toList()
                )
        );

        return new LoginResponse(newAccessToken);
    }

    // ========================= LOGOUT =========================
    @Override
    public void logout(String email, HttpServletResponse response) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        refreshTokenService.deleteByUser(user);

        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/auth");
        cookie.setMaxAge(0); // 🔥 DELETE COOKIE

        response.addCookie(cookie);
    }
}
