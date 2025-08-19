package com.canaryforge.application.port.out;

import com.canaryforge.domain.entities.event.Event;

import reactor.core.publisher.Flux;

public interface EventPublisherPort {
    void publish(Event e);

    Flux<Event> stream();
}
