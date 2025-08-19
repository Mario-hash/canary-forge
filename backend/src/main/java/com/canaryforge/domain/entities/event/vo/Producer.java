package com.canaryforge.domain.entities.event.vo;

import com.canaryforge.domain.entities.event.exceptions.InvalidProducerException;

public record Producer(String value) {
    private static final int MAX = 100;

    public Producer {
        if (value == null || value.isBlank()) {
            throw new InvalidProducerException("producer is required");
        }
        if (value.length() > MAX) {
            throw new InvalidProducerException("producer too long (max " + MAX + ")");
        }
    }
}
