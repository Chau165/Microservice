package com.group1.apigateway.common.exception;

import com.group1.apigateway.common.response.ApiResponse;
import com.group1.apigateway.model.dto.ApiError;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;

@Component
@Order(-2)
public class GlobalErrorExceptionHandler extends AbstractErrorWebExceptionHandler {

    @Value("${spring.application.name:ApiGateway}")
    private String serviceName;

    @Value("${app.version:1.0.0}")
    private String version;

    public GlobalErrorExceptionHandler(ErrorAttributes errorAttributes,
                                       WebProperties webProperties,
                                       ApplicationContext applicationContext,
                                       ServerCodecConfigurer configurer) {
        super(errorAttributes, webProperties.getResources(), applicationContext);
        super.setMessageWriters(configurer.getWriters());
        super.setMessageReaders(configurer.getReaders());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
    }

    private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {

        Map<String, Object> errorAttributes =
                getErrorAttributes(request, ErrorAttributeOptions.defaults());

        Throwable throwable = getError(request);

        int statusCode = (int) errorAttributes.getOrDefault("status", 500);
        HttpStatus status = HttpStatus.resolve(statusCode);
        if (status == null) status = HttpStatus.INTERNAL_SERVER_ERROR;

        String errorMessage = null;
        if (throwable != null && throwable.getMessage() != null) {
            errorMessage = throwable.getMessage();
        }
        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = (String) errorAttributes.getOrDefault("message", null);
        }
        if (errorMessage == null || errorMessage.isBlank()) {
            errorMessage = "Unexpected error";
        }

        String requestId = request.exchange().getRequest().getId();

        ApiResponse<Object> response = ApiResponse.builder()
                .serviceName(serviceName)
                .version(version)
                .requestId(requestId)
                .timestamp(Instant.now())
                .data(null)
                .error(ApiError.builder()
                        .code(status.name())
                        .message(errorMessage)
                        .path(request.path())
                        .build())
                .build();

        return ServerResponse.status(status)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(response));
    }
}