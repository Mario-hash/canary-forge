package com.canaryforge.domain.entities.event.vo;

import com.canaryforge.domain.entities.event.exceptions.InvalidEventTypeException;

public enum EventType {
    CLICK, VIEW, TOKEN_CREATED, TOKEN_USED, ERROR, CUSTOM;

    public static EventType from(String raw) {
        if (raw == null || raw.isBlank()) {
            throw new InvalidEventTypeException("event type is required");
        }
        try {
            return EventType.valueOf(raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new InvalidEventTypeException("unsupported event type: " + raw);
        }
    }
}
