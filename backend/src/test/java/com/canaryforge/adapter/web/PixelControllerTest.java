package com.canaryforge.adapter.web;

import com.canaryforge.adapter.web.controller.PixelController;
import com.canaryforge.application.command.RegisterHitCommand;
import com.canaryforge.application.port.in.RegisterHitUseCase;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;

@WebFluxTest(controllers = PixelController.class)
@Import(PixelControllerTest.Config.class)
class PixelControllerTest {

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
        // Mock: ahora el UC recibe RegisterHitCommand
        given(registerHit.register(any(RegisterHitCommand.class))).willReturn(Mono.empty());

        client.get().uri("/p/SIGPIX")
                .header("User-Agent", "Mozilla/5.0")
                .exchange()
                .expectStatus().isOk()
                // según WebFlux suele incluir charset; si en tu controller cambiaste, ajusta
                // esta aserción
                .expectHeader().valueEquals("Content-Type", "image/svg+xml;charset=UTF-8")
                .expectHeader().valueEquals("Cache-Control", "no-store, no-cache, must-revalidate, max-age=0")
                .expectBody(String.class)
                .value(body -> assertThat(body).contains("<svg"));

        // Capturamos el command para verificar sus campos
        ArgumentCaptor<RegisterHitCommand> captor = ArgumentCaptor.forClass(RegisterHitCommand.class);
        verify(registerHit).register(captor.capture());

        RegisterHitCommand cmd = captor.getValue();
        assertThat(cmd).isNotNull();
        assertThat(cmd.sig()).isEqualTo("SIGPIX");
        assertThat(cmd.userAgent()).isEqualTo("Mozilla/5.0");
        // no enviamos Referer
        assertThat(cmd.referrer()).isNull();
        // ipTrunc depende del request en tests; si tu NetUtil garantiza valor,
        // descomenta:
        // assertThat(cmd.ipTrunc()).isNotBlank();
        // occurredAtIso lo dejamos null para que lo fije ClockPort en el UC
        assertThat(cmd.occurredAtIso()).isNull();
    }
}
