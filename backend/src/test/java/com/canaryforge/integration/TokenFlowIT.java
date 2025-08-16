package com.canaryforge.integration;

import com.canaryforge.adapter.persistence.EventRepository;
import com.canaryforge.adapter.web.dto.TokenResponse; // ⬅️ IMPORTA EL DTO
import java.util.Base64;
import java.util.Map;
import java.security.SecureRandom;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.utility.DockerImageName;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT) // (opcional, recomendable)
@AutoConfigureWebTestClient
class TokenFlowIT {

    static MongoDBContainer mongo = new MongoDBContainer(DockerImageName.parse("mongo:7")); // mejor fija versión
    @Autowired
    EventRepository repo;

    @BeforeAll
    static void start() {
        mongo.start();
    }

    @DynamicPropertySource
    static void props(DynamicPropertyRegistry r) {
        r.add("spring.data.mongodb.uri", mongo::getReplicaSetUrl);
        r.add("CF_SECRET", () -> {
            byte[] b = new byte[32];
            new SecureRandom().nextBytes(b);
            return Base64.getEncoder().encodeToString(b);
        });
    }

    @Autowired
    WebTestClient client;

    @Test
    void createUrlToken_and_click_returns204() {
        var body = Map.of(
                "type", "URL",
                "label", "cv",
                "scenario", "resume",
                "ttlSec", 3600);

        // Crear token y deserializar la respuesta al DTO
        TokenResponse resp = client.post().uri("/api/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(TokenResponse.class)
                .returnResult()
                .getResponseBody();

        assertNotNull(resp);
        assertNotNull(resp.url());
        assertTrue(resp.url().startsWith("/c/"));

        // “Clic” en el cebo → 204 No Content
        client.get().uri(resp.url())
                .exchange()
                .expectStatus().isNoContent();

        assertTrue(repo.count().block() > 0);
    }
}
