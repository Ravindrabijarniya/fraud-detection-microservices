package com.authservice.security;

import com.authservice.model.AuthProvider;
import com.authservice.model.RefreshToken;
import com.authservice.model.Role;
import com.authservice.model.User;
import com.authservice.repository.UserRepository;
import com.authservice.security.oauth.OAuthUserInfo;
import com.authservice.security.oauth.OAuthUserInfoFactory;
import com.authservice.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException {

        OAuth2AuthenticationToken authToken =
                (OAuth2AuthenticationToken) authentication;

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        // GOOGLE / GITHUB
        String registrationId =
                authToken.getAuthorizedClientRegistrationId();

        OAuthUserInfo userInfo =
                OAuthUserInfoFactory.getOAuthUserInfo(
                        registrationId,
                        oauth2User.getAttributes()
                );

        // 🔁 AUTO-SIGNUP OR LOGIN
        User user = userRepository.findByEmail(userInfo.getEmail())
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(userInfo.getEmail());
                    newUser.setName(userInfo.getName());
                    newUser.setProvider(
                            AuthProvider.valueOf(registrationId.toUpperCase())
                    );
                    newUser.setProviderId(userInfo.getId());
                    newUser.setRoles(Collections.singleton(Role.USER));
                    return userRepository.save(newUser);
                });

        RefreshToken refreshToken =
                refreshTokenService.createOrUpdateRefreshToken(user);

        ResponseCookie cookie=ResponseCookie.from("refreshToken", refreshToken.getToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(7*24*60*60)
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        String accessToken= jwtUtil.generateToken(
                user.getEmail(),
                Map.of("roles",user.getRoles())
        );

        response.sendRedirect(
                "http://localhost:3000/oauth-success?token=" + accessToken
        );

        // 🔐 JWT
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", user.getRoles());

        String token = jwtUtil.generateToken(user.getEmail(), claims);

        response.sendRedirect(
                "http://localhost:3000/oauth-success?token=" + token
        );
    }
}
