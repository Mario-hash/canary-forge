package com.canaryforge.application.service;

import com.canaryforge.application.port.in.RegisterHitUseCase;
import com.canaryforge.application.port.out.ClockPort;
import com.canaryforge.application.port.out.EventPublisherPort;
import com.canaryforge.application.port.out.EventStorePort;
import com.canaryforge.application.port.out.TokenSignerPort;
import com.canaryforge.domain.event.Event;
import com.canaryforge.domain.event.Severity;
import com.canaryforge.domain.token.TokenPayload;

import reactor.core.publisher.Mono;

public class RegisterHitService implements RegisterHitUseCase {

    private final TokenSignerPort signer;
    private final EventStorePort store;
    private final EventPublisherPort pub;
    private final ClockPort clock;

    public RegisterHitService(TokenSignerPort signer, EventStorePort store, EventPublisherPort pub, ClockPort clock) {
        this.signer = signer;
        this.store = store;
        this.pub = pub;
        this.clock = clock;
    }

    @Override
    public Mono<Event> register(String sig, String ua, String referrer, String ipTrunc) {
        TokenPayload tp = signer.verify(sig); // lanza si inválido/expirado
        Severity sev = isPreviewer(ua, referrer) ? Severity.LOW : Severity.MEDIUM;
        Event ev = new Event(
                null,
                "HIT",
                tp.t(),
                tp.label(),
                tp.sc(),
                new Event.Source(ipTrunc, ua, referrer),
                sev,
                clock.now());
        return store.save(ev)
                .doOnNext(pub::publish);
    }

    private boolean isPreviewer(String ua, String ref) {
        String s = (ua == null ? "" : ua).toLowerCase();
        return s.contains("slack") || s.contains("twitterbot") || s.contains("linkedin") || s.contains("discord");
    }

}
