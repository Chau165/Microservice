package com.group1.apigateway;

import com.github.tomakehurst.wiremock.http.Fault;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWireMock(port = 0)
@Import(TestSecurityConfig.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MetadataServiceCircuitBreakerTest {

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void shouldReturn504WhenMetadataServiceIsSlow() {

        stubFor(get(urlPathEqualTo("/metadata/info"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withFixedDelay(5000)
                        .withHeader("Content-Type", MediaType.APPLICATION_JSON_VALUE)));

        webTestClient.get()
                .uri("/api/metadata/info")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.GATEWAY_TIMEOUT);
    }

    @Test
    void shouldOpenCircuitAfterMultipleFailures() {

        stubFor(get(urlPathEqualTo("/metadata/info"))
                .willReturn(aResponse()
                        .withFault(Fault.CONNECTION_RESET_BY_PEER)));

        for (int i = 0; i < 2; i++) {
            webTestClient.get()
                    .uri("/api/metadata/info")
                    .exchange()
                    .expectStatus()
                    .is5xxServerError();
        }

        webTestClient.get()
                .uri("/api/metadata/info")
                .exchange()
                .expectStatus()
                .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);

        verify(lessThanOrExactly(2),
                getRequestedFor(urlPathEqualTo("/metadata/info")));
    }
}