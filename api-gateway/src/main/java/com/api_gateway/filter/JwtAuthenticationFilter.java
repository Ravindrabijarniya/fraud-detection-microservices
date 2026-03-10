//package com.api_gateway.filter;
//
//import com.api_gateway.service.RateLimiterService;
//import com.api_gateway.util.JwtUtil;
//import io.jsonwebtoken.Claims;
//import lombok.RequiredArgsConstructor;
//import org.springframework.cloud.gateway.filter.GatewayFilter;
//import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Component;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//
//import java.time.Duration;
//
//@Component
////@RequiredArgsConstructor
//public class JwtAuthenticationFilter
//        extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {
//
//    private final JwtUtil jwtUtil;
//    private final RateLimiterService rateLimiterService;
//
//    // ✅ REQUIRED by GatewayFilterFactory
//    public JwtAuthenticationFilter(JwtUtil jwtUtil,
//                                   RateLimiterService rateLimiterService) {
//        super(Config.class);
//        this.jwtUtil = jwtUtil;
//        this.rateLimiterService = rateLimiterService;
//    }
//
//    @Override
//    public GatewayFilter apply(Config config) {
//
//        return (exchange, chain) -> {
//
//            String path = exchange.getRequest().getURI().getPath();
//
//            // 🔓 Public endpoints
//            if (path.startsWith("/auth") || path.startsWith("/oauth2")) {
//                return chain.filter(exchange);
//            }
//
//            // 🔑 Authorization header
//            String authHeader = exchange.getRequest()
//                    .getHeaders()
//                    .getFirst(HttpHeaders.AUTHORIZATION);
//
//            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
//                return unauthorized(exchange);
//            }
//
//            String token = authHeader.substring(7);
//
//            Claims claims;
//            try {
//                claims = jwtUtil.getClaims(token);
//            } catch (Exception e) {
//                return unauthorized(exchange);
//            }
//
//            String email = claims.getSubject();
//            String role = claims.get("role", String.class);
//
//            if (email == null || role == null) {
//                return forbidden(exchange);
//            }
//
//            // 🧠 ROLE BASED ACCESS
//            if (path.startsWith("/fraud") && !role.equals("ADMIN")) {
//                return forbidden(exchange);
//            }
//
//            if (path.startsWith("/transactions")
//                    && !(role.equals("USER") || role.equals("ADMIN"))) {
//                return forbidden(exchange);
//            }
//
//            if (path.startsWith("/notifications")
//                    && !(role.equals("USER") || role.equals("ADMIN"))) {
//                return forbidden(exchange);
//            }
//
//            // 🚦 USER RATE LIMIT
//            return rateLimiterService
//                    .isAllowed("user:" + email, 10, Duration.ofMinutes(1))
//                    .flatMap(allowed -> {
//                        if (!allowed) {
//                            return tooManyRequests(exchange);
//                        }
//
//                        ServerWebExchange mutated = exchange.mutate()
//                                .request(exchange.getRequest().mutate()
//                                        .header("X-User-Email", email)
//                                        .header("X-User-Role", role)
//                                        .build())
//                                .build();
//
//                        return chain.filter(mutated);
//                    });
//        };
//    }
//
//    private Mono<Void> unauthorized(ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//        return exchange.getResponse().setComplete();
//    }
//
//    private Mono<Void> forbidden(ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
//        return exchange.getResponse().setComplete();
//    }
//
//    private Mono<Void> tooManyRequests(ServerWebExchange exchange) {
//        exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
//        return exchange.getResponse().setComplete();
//    }
//
//    public static class Config {
//    }
//}
package com.api_gateway.filter;

import com.api_gateway.service.RateLimiterService;
import com.api_gateway.util.JwtUtil;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@Component
public class JwtAuthenticationFilter
        extends AbstractGatewayFilterFactory<JwtAuthenticationFilter.Config> {

    private final JwtUtil jwtUtil;
    private final RateLimiterService rateLimiterService;

    // ✅ SINGLE CONSTRUCTOR
    public JwtAuthenticationFilter(JwtUtil jwtUtil,
                                   RateLimiterService rateLimiterService) {
        super(Config.class);
        this.jwtUtil = jwtUtil;
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public GatewayFilter apply(Config config) {

        return (exchange, chain) -> {

            String path = exchange.getRequest().getURI().getPath();

            // ✅ Public endpoints
            if (path.startsWith("/auth")) {
                return chain.filter(exchange);
            }

            String authHeader = exchange.getRequest()
                    .getHeaders()
                    .getFirst(HttpHeaders.AUTHORIZATION);

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return unauthorized(exchange);
            }

            String token = authHeader.substring(7);

            Claims claims;
            try {
                claims = jwtUtil.validateAndGetClaims(token);
            } catch (Exception e) {
                return unauthorized(exchange);
            }

            String email = jwtUtil.getEmail(claims);
            List<String> roles = jwtUtil.getRoles(claims);

            if (roles == null || roles.isEmpty()) {
                return forbidden(exchange);
            }

            // 🔐 ROLE BASED ACCESS
            if (path.startsWith("/notifications/admin") && !roles.contains("ADMIN")) {
                return forbidden(exchange);
            }

            if (path.startsWith("/transactions")
                    && !(roles.contains("USER") || roles.contains("ADMIN"))) {
                return forbidden(exchange);
            }

            // 🚦 RATE LIMIT
            return rateLimiterService
                    .isAllowed("user:" + email, 20, Duration.ofMinutes(1))
                    .flatMap(allowed -> {
                        if (!allowed) {
                            exchange.getResponse()
                                    .setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
                            return exchange.getResponse().setComplete();
                        }

                        ServerWebExchange mutated = exchange.mutate()
                                .request(exchange.getRequest().mutate()
                                        .header("X-User-Email", email)
                                        .header("X-User-Roles", String.join(",", roles))
                                        .build())
                                .build();

                        return chain.filter(mutated);
                    });
        };
    }

    private Mono<Void> unauthorized(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
        return exchange.getResponse().setComplete();
    }

    private Mono<Void> forbidden(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
        return exchange.getResponse().setComplete();
    }

    public static class Config {
    }
}

