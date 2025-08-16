package com.canaryforge.application.port.out;

import com.canaryforge.domain.event.Event;

import reactor.core.publisher.Mono;

public interface EventStorePort {
    Mono<Event> save(Event e);
}
