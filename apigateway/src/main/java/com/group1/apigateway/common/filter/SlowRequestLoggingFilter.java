package com.group1.apigateway.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class SlowRequestLoggingFilter implements GlobalFilter, Ordered {

    @Value("${app.slow-request.threshold-ms:2000}")
    private long thresholdMs;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {

        long startTime = System.currentTimeMillis();

        return chain.filter(exchange)
                .doFinally(signal -> {
                    long duration = System.currentTimeMillis() - startTime;

                    if (duration > thresholdMs) {
                        String requestId = exchange.getRequest().getId();
                        String path = exchange.getRequest().getURI().getPath();

                        log.warn("Slow request detected | requestId={} | path={} | time={}ms",
                                requestId, path, duration);
                    }
                });
    }

    @Override
    public int getOrder() {
        return -1;
    }
}
