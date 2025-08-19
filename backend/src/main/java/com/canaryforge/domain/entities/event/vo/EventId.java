package com.canaryforge.domain.entities.event.vo;

import java.util.UUID;

public record EventId(UUID value) {
    public EventId {
        if (value == null)
            throw new IllegalArgumentException("EventId cannot be null");
    }

    public static EventId newId() {
        return new EventId(UUID.randomUUID());
    }

    public static EventId fromString(String s) {
        return new EventId(UUID.fromString(s));
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
