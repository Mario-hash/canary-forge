package com.canaryforge.adapter.web;

import com.canaryforge.adapter.web.controller.ClickController;
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
import static org.mockito.Mockito.verify;
import static org.mockito.ArgumentMatchers.any;

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
        // Mock: ahora el use case recibe RegisterHitCommand
        given(registerHit.register(any(RegisterHitCommand.class)))
                .willReturn(Mono.empty());

        client.get().uri("/c/SIG123")
                .header("User-Agent", "Mozilla/5.0")
                .exchange()
                .expectStatus().isNoContent();

        // Capturamos el command enviado al use case
        ArgumentCaptor<RegisterHitCommand> captor = ArgumentCaptor.forClass(RegisterHitCommand.class);
        verify(registerHit).register(captor.capture());

        RegisterHitCommand cmd = captor.getValue();
        assertThat(cmd).isNotNull();
        assertThat(cmd.sig()).isEqualTo("SIG123");
        assertThat(cmd.userAgent()).isEqualTo("Mozilla/5.0");
        assertThat(cmd.referrer()).isNull(); // no mandamos header Referer
        // ipTrunc puede ser null en tests (depende del ServerHttpRequest); no lo
        // asertamos
        // occurredAtIso lo ponemos a null en el controller para que lo fije ClockPort
        // -> tampoco asertamos
    }
}
