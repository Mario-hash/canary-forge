package com.canaryforge.adapter.web;

import com.canaryforge.adapter.web.dto.TokenResponse;
import com.canaryforge.application.command.CreateTokenCommand;
import com.canaryforge.application.port.in.CreateTokenUseCase;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

@WebFluxTest(controllers = TokenController.class)
@Import(TokenControllerTest.Config.class)
class TokenControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        CreateTokenUseCase createTokenUseCase() {
            return Mockito.mock(CreateTokenUseCase.class);
        }
    }

    @BeforeEach
    void resetMock() {
        Mockito.reset(createToken);
    }

    @Autowired
    WebTestClient client;

    @Autowired
    CreateTokenUseCase createToken;

    @Test
    void create_returns_clean_cebo_url() {

        BDDMockito.given(createToken.create(Mockito.any(CreateTokenCommand.class)))
                .willReturn("SIG123");

        client.post().uri("/api/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("type", "URL", "label", "cv", "scenario", "resume", "ttlSec", 3600))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody()
                .jsonPath("$.url").isEqualTo("/c/SIG123");

        ArgumentCaptor<CreateTokenCommand> cap = ArgumentCaptor.forClass(CreateTokenCommand.class);
        verify(createToken).create(cap.capture());
        CreateTokenCommand cmd = cap.getValue();
        assertThat(cmd.type()).isEqualTo("URL");
        assertThat(cmd.label()).isEqualTo("cv");
        assertThat(cmd.scenario()).isEqualTo("resume");
        assertThat(cmd.ttlSec()).isEqualTo(3600);
    }

    @Test
    void create_pix_returns_url_and_img_snippet() {
        BDDMockito.given(createToken.create(Mockito.any(CreateTokenCommand.class)))
                .willReturn("SIGPIX");

        var resp = client.post().uri("/api/tokens")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(Map.of("type", "PIX", "label", "cv", "scenario", "resume", "ttlSec", 3600))
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(TokenResponse.class)
                .returnResult()
                .getResponseBody();

        assertThat(resp).isNotNull();
        assertThat(resp.url()).isEqualTo("/p/SIGPIX");
        assertThat(resp.html()).contains("src=\"/p/SIGPIX\"");

        ArgumentCaptor<CreateTokenCommand> cap = ArgumentCaptor.forClass(CreateTokenCommand.class);
        verify(createToken).create(cap.capture());
        CreateTokenCommand cmd = cap.getValue();
        assertThat(cmd.type()).isEqualTo("PIX");
        assertThat(cmd.label()).isEqualTo("cv");
        assertThat(cmd.scenario()).isEqualTo("resume");
        assertThat(cmd.ttlSec()).isEqualTo(3600);
    }
}
