package com.canaryforge.adapter.web;

import com.canaryforge.application.port.out.EventPublisherPort;
import com.canaryforge.domain.event.Event;
import com.canaryforge.domain.event.Severity;
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

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest(controllers = EventStreamController.class)
@Import(EventStreamControllerTest.Config.class)
class EventStreamControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        Sinks.Many<Event> eventSink() {
            // 🔁 evita la carrera: reenvía el último a nuevos suscriptores
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
        return new Event(
                "e1", "HIT", "URL", "cv", "resume",
                new Event.Source("0.0.0.0/24", "JUnit", null),
                Severity.MEDIUM, Instant.now());
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
                .returnResult(new ParameterizedTypeReference<ServerSentEvent<Event>>() {
                });

        StepVerifier.create(result.getResponseBody().map(ServerSentEvent::data))
                .assertNext(e -> {
                    assertThat(e).isNotNull();
                    assertThat(e.label()).isEqualTo("cv");
                    assertThat(e.type()).isEqualTo("HIT");
                    assertThat(e.tokenType()).isEqualTo("URL");
                    assertThat(e.severity()).isEqualTo(Severity.MEDIUM);
                })
                .thenCancel()
                .verify();
    }
}
