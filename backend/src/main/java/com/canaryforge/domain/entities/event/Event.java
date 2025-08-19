package com.canaryforge.domain.entities.event;

import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.event.vo.*;

import java.util.Objects;

public final class Event {
    private final EventId id;
    private final EventType type;
    private final OccurredAt occurredAt;
    private final Producer producer;
    private final Version version;
    private final CorrelationId correlationId; // opcional
    private final CausationId causationId; // opcional
    private final JsonPayload payload; // opcional
    private final Attributes attributes; // opcional (vacío por defecto)

    private Event(EventId id,
            EventType type,
            OccurredAt occurredAt,
            Producer producer,
            Version version,
            CorrelationId correlationId,
            CausationId causationId,
            JsonPayload payload,
            Attributes attributes) {
        this.id = Objects.requireNonNull(id, "id");
        this.type = Objects.requireNonNull(type, "type");
        this.occurredAt = Objects.requireNonNull(occurredAt, "occurredAt");
        this.producer = Objects.requireNonNull(producer, "producer");
        this.version = Objects.requireNonNull(version, "version");
        this.correlationId = correlationId;
        this.causationId = causationId;
        this.payload = payload;
        this.attributes = attributes == null ? Attributes.empty() : attributes;
    }

    public static Event create(EventType type,
            OccurredAt occurredAt,
            Producer producer,
            Version version,
            CorrelationId correlationId,
            CausationId causationId,
            JsonPayload payload,
            Attributes attributes) {
        return new Event(
                EventId.newId(),
                type, occurredAt, producer, version,
                correlationId, causationId, payload, attributes);
    }

    public static Event restore(EventId id,
            EventType type,
            OccurredAt occurredAt,
            Producer producer,
            Version version,
            CorrelationId correlationId,
            CausationId causationId,
            JsonPayload payload,
            Attributes attributes) {
        return new Event(id, type, occurredAt, producer, version, correlationId, causationId, payload, attributes);
    }

    public EventId id() {
        return id;
    }

    public EventType type() {
        return type;
    }

    public OccurredAt occurredAt() {
        return occurredAt;
    }

    public Producer producer() {
        return producer;
    }

    public Version version() {
        return version;
    }

    public CorrelationId correlationId() {
        return correlationId;
    }

    public CausationId causationId() {
        return causationId;
    }

    public JsonPayload payload() {
        return payload;
    }

    public Attributes attributes() {
        return attributes;
    }
}
