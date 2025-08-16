package com.canaryforge.application.port.in;

import com.canaryforge.domain.event.Event;

import reactor.core.publisher.Mono;

public interface RegisterHitUseCase {
    Mono<Event> register(String sig, String ua, String referrer, String ipTrunc);

}
