package com.group1.apigateway.common.filter;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 20)
public class MdcLoggingWebFilter implements WebFilter {

    private static final String REQUEST_ID_HEADER = "X-Request-ID";

    @Override
    @NonNull
    public Mono<Void> filter(@NonNull ServerWebExchange exchange,
                             @NonNull WebFilterChain chain) {

        String requestId = exchange.getRequest()
                .getHeaders()
                .getFirst(REQUEST_ID_HEADER);

        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        String finalRequestId = requestId;

        ServerWebExchange mutatedExchange = exchange.mutate()
                .request(builder ->
                        builder.header(REQUEST_ID_HEADER, finalRequestId))
                .build();

        mutatedExchange.getResponse()
                .getHeaders()
                .add(REQUEST_ID_HEADER, finalRequestId);

        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Optional::of)
                .defaultIfEmpty(Optional.empty())
                .flatMap(optAuth -> {

                    populateMdc(finalRequestId, optAuth.orElse(null));

                    return chain.filter(mutatedExchange)
                            .doFinally(signal -> MDC.clear());
                });
    }

    private void populateMdc(String requestId, Authentication auth) {

        MDC.put("requestId", requestId);

        if (auth != null && auth.isAuthenticated()) {

            MDC.put("userId", auth.getName());

            String role = auth.getAuthorities()
                    .stream()
                    .findFirst()
                    .map(GrantedAuthority::getAuthority)
                    .orElse("NONE");

            MDC.put("role", role);

        } else {

            MDC.put("userId", "ANONYMOUS");
            MDC.put("role", "NONE");
        }
    }
}