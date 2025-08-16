package com.canaryforge.adapter.persistence;

import com.canaryforge.application.port.out.EventStorePort;
import com.canaryforge.domain.event.Event;

import reactor.core.publisher.Mono;

public class MongoEventStoreAdapter implements EventStorePort {

    private final EventRepository repo;

    public MongoEventStoreAdapter(EventRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Event> save(Event e) {
        EventDoc d = new EventDoc();
        d.type = e.type();
        d.tokenType = e.tokenType();
        d.label = e.label();
        d.scenario = e.scenario();
        d.ipTrunc = e.source() == null ? null : e.source().ipTrunc();
        d.ua = e.source() == null ? null : e.source().ua();
        d.referrer = e.source() == null ? null : e.source().referrer();
        d.severity = e.severity() == null ? null : e.severity().name();
        d.createdAt = e.createdAt();
        return repo.save(d).map(saved -> new Event(
                saved.id,
                saved.type,
                saved.tokenType,
                saved.label,
                saved.scenario,
                new Event.Source(saved.ipTrunc, saved.ua, saved.referrer),
                saved.severity == null ? null : com.canaryforge.domain.event.Severity.valueOf(saved.severity),
                saved.createdAt));
    }

}
