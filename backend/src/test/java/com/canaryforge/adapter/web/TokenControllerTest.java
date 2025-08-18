package com.canaryforge.adapter.web;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.eq;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.canaryforge.application.port.in.CreatePixTokenUseCase;
import com.canaryforge.application.port.in.CreateUrlTokenUseCase;

@WebFluxTest(controllers = TokenController.class)
@Import(TokenControllerTest.Config.class)
class TokenControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        CreateUrlTokenUseCase createUrlTokenUseCase() {
            return Mockito.mock(CreateUrlTokenUseCase.class);
        }

        @Bean
        CreatePixTokenUseCase createPixTokenUseCase() { // ⬅️ añadir este bean
            return Mockito.mock(CreatePixTokenUseCase.class);
        }
    }

    @Autowired
    WebTestClient client;
    @Autowired
    CreateUrlTokenUseCase createUrl;
    @Autowired
    CreatePixTokenUseCase createPix;

    @Test
    void create_returns_clean_cebo_url() {
        BDDMockito.given(createUrl.create(eq("cv"), eq("resume"), eq(3600)))
                .willReturn("SIG123");

        client.post().uri("/api/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("type", "URL", "label", "cv", "scenario", "resume", "ttlSec", 3600))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.url").isEqualTo("/c/SIG123");
    }

    @Test
    void create_pix_returns_url_and_img_snippet() {
        BDDMockito.given(createPix.create(eq("cv"), eq("resume"), eq(3600)))
                .willReturn("SIGPIX");

        client.post().uri("/api/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("type", "PIX", "label", "cv", "scenario", "resume", "ttlSec", 3600))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.url").isEqualTo("/p/SIGPIX")
                .jsonPath("$.html").value(v -> {
                    String html = String.valueOf(v);
                    assertTrue(html.contains("src=\"/p/SIGPIX\""), "HTML debe incluir el <img> apuntando al pixel");
                });
    }
}
