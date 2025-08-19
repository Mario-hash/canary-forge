package com.canaryforge.adapter.realtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.canaryforge.application.port.out.EventPublisherPort;
import com.canaryforge.domain.entities.event.Event;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SseEventPublisherAdapter implements EventPublisherPort {
    private static final Logger log = LoggerFactory.getLogger(SseEventPublisherAdapter.class);
    private final Sinks.Many<Event> sink = Sinks.many().replay().limit(50);

    @Override
    public Flux<Event> stream() {
        return sink.asFlux();
    }

    @Override
    public void publish(Event e) {
        var r = sink.tryEmitNext(e);
        if (r.isFailure()) {
            log.warn("SSE sink emit result: {}", r);
        }
    }
}
