package com.canaryforge.adapter.web;

import com.canaryforge.adapter.web.controller.EventStreamController;
import com.canaryforge.adapter.web.dto.EventSseDto;
import com.canaryforge.application.port.out.EventPublisherPort;
import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.event.Event;
import com.canaryforge.domain.entities.event.vo.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.test.StepVerifier;

import java.time.Instant;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(controllers = EventStreamController.class)
@Import(EventStreamControllerTest.Config.class)
class EventStreamControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        Sinks.Many<Event> eventSink() {
            // Reemite el último para nuevos suscriptores y evita condiciones de carrera
            return Sinks.many().replay().limit(1);
        }

        @Bean
        EventPublisherPort eventPublisherPort(Sinks.Many<Event> sink) {
            return new EventPublisherPort() {
                @Override
                public void publish(Event e) {
                    sink.tryEmitNext(e);
                }

                @Override
                public Flux<Event> stream() {
                    return sink.asFlux();
                }
            };
        }
    }

    @Autowired
    WebTestClient client;
    @Autowired
    Sinks.Many<Event> sink;

    private static Event sample() {
        Attributes attrs = Attributes.of(Map.of(
                "label", "cv",
                "tokenType", "URL",
                "scenario", "resume",
                "ipTrunc", "0.0.0.0/24",
                "ua", "JUnit"
        // si quieres severity: "severity","MEDIUM"
        ));

        return Event.create(
                EventType.from("CLICK"),
                new OccurredAt(Instant.now()),
                new Producer("test"),
                Version.of(1),
                null, // CorrelationId
                null, // CausationId
                JsonPayload.of(null),
                attrs);
    }

    @Test
    void stream_returns_text_event_stream_and_no_store_header() {
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            sink.tryEmitNext(sample());
        }).start();

        client.get().uri("/api/events/stream")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentTypeCompatibleWith(MediaType.TEXT_EVENT_STREAM)
                .expectHeader().valueEquals("Cache-Control", "no-store");
    }

    @Test
    void stream_emits_events_as_sse_data() {
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ignored) {
            }
            sink.tryEmitNext(sample());
        }).start();

        var result = client.get().uri("/api/events/stream")
                .exchange()
                .expectStatus().isOk()
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<EventSseDto>>() {
                });

        StepVerifier.create(result.getResponseBody().map(ServerSentEvent::data))
                .assertNext(dto -> {
                    assertThat(dto).isNotNull();
                    assertThat(dto.type()).isEqualTo("CLICK");
                    assertThat(dto.attributes().get("label")).isEqualTo("cv");
                    assertThat(dto.attributes().get("tokenType")).isEqualTo("URL");
                    assertThat(dto.attributes().get("scenario")).isEqualTo("resume");
                })
                .thenCancel()
                .verify();
    }
}
