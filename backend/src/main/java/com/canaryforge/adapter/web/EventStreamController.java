package com.canaryforge.adapter.web;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canaryforge.application.port.out.EventPublisherPort;
import com.canaryforge.domain.event.Event;

import reactor.core.publisher.Flux;

@RestController
public class EventStreamController {
    private final EventPublisherPort pub;

    public EventStreamController(EventPublisherPort pub) {
        this.pub = pub;
    }

    @GetMapping(value = "/api/events/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Event> stream() {
        return pub.stream();
    }
}
