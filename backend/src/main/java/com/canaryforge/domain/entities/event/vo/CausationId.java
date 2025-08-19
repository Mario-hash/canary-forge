package com.canaryforge.domain.entities.event.vo;

import com.canaryforge.domain.entities.event.exceptions.InvalidCausationIdException;
import java.util.UUID;

public record CausationId(UUID value) {
    public CausationId {
        if (value == null)
            throw new InvalidCausationIdException("causationId cannot be null");
    }

    public static CausationId fromString(String s) {
        try {
            return new CausationId(UUID.fromString(s));
        } catch (Exception e) {
            throw new InvalidCausationIdException("invalid causationId (UUID expected)");
        }
    }
}
