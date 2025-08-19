package com.canaryforge.application.port.in;

import com.canaryforge.application.command.RegisterHitCommand;
import com.canaryforge.domain.entities.event.Event;

import reactor.core.publisher.Mono;

public interface RegisterHitUseCase {
    Mono<Event> register(RegisterHitCommand cmd);

}
