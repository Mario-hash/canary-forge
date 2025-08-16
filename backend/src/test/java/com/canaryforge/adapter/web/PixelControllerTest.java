package com.canaryforge.adapter.web;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.verify;
import static org.mockito.BDDMockito.given;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.canaryforge.application.port.in.RegisterHitUseCase;

import reactor.core.publisher.Mono;

@WebFluxTest(controllers = PixelController.class)
@Import(PixelControllerTest.Config.class)
public class PixelControllerTest {
    @TestConfiguration
    static class Config {
        @Bean
        RegisterHitUseCase registerHitUseCase() {
            return Mockito.mock(RegisterHitUseCase.class);
        }
    }

    @Autowired
    WebTestClient client;
    @Autowired
    RegisterHitUseCase registerHit;

    @Test
    void pixel_returns_svg_and_calls_usecase() {
        given(registerHit.register(eq("SIGPIX"), any(), any(), any())).willReturn(Mono.empty());

        client.get().uri("/p/SIGPIX")
                .header("User-Agent", "Mozilla/5.0")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().valueEquals("Content-Type", "image/svg+xml;charset=UTF-8")
                .expectHeader().valueEquals("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                .expectBody(String.class).value(body -> {
                    assert body.contains("<svg");
                });

        verify(registerHit).register(eq("SIGPIX"), eq("Mozilla/5.0"), isNull(), any());
    }
}
