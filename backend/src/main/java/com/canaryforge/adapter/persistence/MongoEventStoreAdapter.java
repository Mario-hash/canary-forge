package com.canaryforge.adapter.persistence;

import com.canaryforge.application.port.out.EventStorePort;
import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.event.Event;
import com.canaryforge.domain.entities.event.vo.*;

import reactor.core.publisher.Mono;

public class MongoEventStoreAdapter implements EventStorePort {

    private final EventRepository repo;

    public MongoEventStoreAdapter(EventRepository repo) {
        this.repo = repo;
    }

    @Override
    public Mono<Event> save(Event e) {
        // dominio -> doc
        EventDoc d = new EventDoc();
        d.id = e.id().toString(); // UUID string
        d.type = e.type().name();
        d.producer = e.producer().value();
        d.version = e.version().value();
        d.occurredAt = e.occurredAt().value();
        d.correlationId = e.correlationId() == null ? null : e.correlationId().value().toString();
        d.causationId = e.causationId() == null ? null : e.causationId().value().toString();
        d.payloadJson = e.payload() == null ? null : e.payload().raw();
        d.attributes = e.attributes() == null ? null : e.attributes().value();

        return repo.save(d).map(saved -> {
            // doc -> dominio (usa restore)
            return Event.restore(
                    EventId.fromString(saved.id),
                    EventType.from(saved.type),
                    new OccurredAt(saved.occurredAt),
                    new Producer(saved.producer),
                    Version.of(saved.version == null ? 1 : saved.version),
                    saved.correlationId == null ? null : CorrelationId.fromString(saved.correlationId),
                    saved.causationId == null ? null : CausationId.fromString(saved.causationId),
                    JsonPayload.of(saved.payloadJson),
                    Attributes.of(saved.attributes));
        });
    }
}
