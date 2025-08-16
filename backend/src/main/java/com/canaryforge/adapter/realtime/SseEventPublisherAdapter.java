package com.canaryforge.adapter.realtime;

import com.canaryforge.application.port.out.EventPublisherPort;
import com.canaryforge.domain.event.Event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SseEventPublisherAdapter implements EventPublisherPort {
    private final Sinks.Many<Event> sink = Sinks.many().multicast().onBackpressureBuffer();

    @Override
    public Flux<Event> stream() {
        return sink.asFlux();
    }

    @Override
    public void publish(Event e) {
        sink.tryEmitNext(e);
    }
}
