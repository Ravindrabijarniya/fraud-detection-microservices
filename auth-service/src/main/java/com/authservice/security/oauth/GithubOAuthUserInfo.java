package com.authservice.security.oauth;

import java.util.Map;

public class GithubOAuthUserInfo implements OAuthUserInfo {

    private final Map<String, Object> attributes;

    public GithubOAuthUserInfo(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    @Override
    public String getId() {
        return String.valueOf(attributes.get("id"));
    }

    @Override
    public String getName() {
        return (String) attributes.getOrDefault("name", "GitHub User");
    }

    @Override
    public String getEmail() {
        // GitHub may not return email
        Object email = attributes.get("email");

        if (email == null) {
            // fallback: create pseudo-email
            return attributes.get("login") + "@github.com";
        }
        return email.toString();
    }
}
