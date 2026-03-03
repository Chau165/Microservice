package com.group1.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;

@SpringBootTest
class SlowRequestLoggingFilterTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldLogSlowRequest() {
        webTestClient.get()
                .uri("/some-endpoint")
                .exchange()
                .expectStatus().isOk();
    }
}
