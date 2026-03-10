package com.api_gateway.filter;

import org.slf4j.MDC;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Component
public class TraceIdFilter implements GatewayFilter, Ordered {

    public static final String TRACE_ID = "traceId";

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        String traceId = UUID.randomUUID().toString();

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(exchange.getRequest().mutate()
                        .header("X-Trace-Id", traceId)
                        .build())
                .build();

        return chain.filter(mutatedExchange)
                .contextWrite(ctx -> ctx.put(TRACE_ID, traceId))
                .doOnEach(signal -> {
                    if (signal.isOnNext() || signal.isOnComplete()) {
                        signal.getContextView()
                                .getOrEmpty(TRACE_ID)
                                .ifPresent(id -> MDC.put(TRACE_ID, id.toString()));
                    }
                })
                .doFinally(signal -> MDC.clear());
    }

    @Override
    public int getOrder() {
        return -2; // MUST run before JwtAuthenticationFilter
    }
}
