package com.canaryforge.adapter.web.controller;

import java.time.Duration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canaryforge.adapter.web.dto.EventSseDto;
import com.canaryforge.adapter.web.mapper.EventWebMapper;
import com.canaryforge.application.port.out.EventPublisherPort;

import reactor.core.publisher.Flux;

@RestController
public class EventStreamController {
    private static final Logger log = LoggerFactory.getLogger(EventStreamController.class);
    private final EventPublisherPort pub;

    public EventStreamController(EventPublisherPort pub) {
        this.pub = pub;
    }

    @GetMapping(value = "/api/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public ResponseEntity<Flux<ServerSentEvent<?>>> stream() {
        var events = pub.stream()
                .doOnSubscribe(s -> log.info("SSE subscriber +1"))
                .doFinally(sig -> log.info("SSE subscriber -1 ({})", sig))
                .map(EventWebMapper::toSse)
                .map(dto -> ServerSentEvent.builder(dto).build());

        var heartbeat = Flux.interval(Duration.ofSeconds(15))
                .map(i -> ServerSentEvent.<EventSseDto>builder().comment("hb").build());

        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "no-store")
                .body(Flux.merge(heartbeat, events));
    }
}
