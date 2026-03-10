package com.authservice.security.oauth;

import java.util.Map;

public class OAuthUserInfoFactory {

    public static OAuthUserInfo getOAuthUserInfo(
            String registrationId,
            Map<String, Object> attributes
    ) {
        if ("google".equalsIgnoreCase(registrationId)) {
            return new GoogleOAuthUserInfo(attributes);
        }
        if ("github".equalsIgnoreCase(registrationId)) {
            return new GithubOAuthUserInfo(attributes);
        }
        throw new IllegalArgumentException(
                "Unsupported OAuth provider: " + registrationId
        );
    }
}
