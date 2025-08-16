package com.canaryforge.adapter.web;

import com.canaryforge.application.port.in.CreateUrlTokenUseCase;
import com.canaryforge.application.port.in.RegisterHitUseCase;

import reactor.core.publisher.Mono;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;

@WebFluxTest(controllers = ClickController.class)
@Import(ClickControllerTest.Config.class)
class ClickControllerTest {

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
    void click_returns_204_and_calls_usecase() {
        given(registerHit.register(eq("SIG123"), any(), any(), any()))
                .willReturn(Mono.empty());

        client.get().uri("/c/SIG123")
                .header("User-Agent", "Mozilla/5.0")
                .exchange()
                .expectStatus().isNoContent();

        // Verifica que se llamó con los datos esperados
        verify(registerHit).register(eq("SIG123"), eq("Mozilla/5.0"), isNull(), any());
    }

}
