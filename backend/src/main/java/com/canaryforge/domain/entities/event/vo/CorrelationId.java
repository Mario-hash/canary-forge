package com.canaryforge.domain.entities.event.vo;

import com.canaryforge.domain.entities.event.exceptions.InvalidCorrelationIdException;
import java.util.UUID;

public record CorrelationId(UUID value) {
    public CorrelationId {
        if (value == null)
            throw new InvalidCorrelationIdException("correlationId cannot be null");
    }

    public static CorrelationId fromString(String s) {
        try {
            return new CorrelationId(UUID.fromString(s));
        } catch (Exception e) {
            throw new InvalidCorrelationIdException("invalid correlationId (UUID expected)");
        }
    }
}
