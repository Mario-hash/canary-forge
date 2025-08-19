package com.canaryforge.domain.entities.event.vo;

import com.canaryforge.domain.entities.event.exceptions.InvalidOccurredAtException;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

public record OccurredAt(Instant value) {
    private static final Duration FUTURE_SKEW_TOLERANCE = Duration.ofMinutes(5);

    public OccurredAt {
        if (value == null)
            throw new InvalidOccurredAtException("occurredAt is required");
        Instant now = Instant.now(Clock.systemUTC());
        if (value.isAfter(now.plus(FUTURE_SKEW_TOLERANCE))) {
            throw new InvalidOccurredAtException("occurredAt is too far in the future");
        }
    }

    public static OccurredAt parseIso(String iso) {
        try {
            return new OccurredAt(Instant.parse(iso));
        } catch (Exception e) {
            throw new InvalidOccurredAtException("invalid occurredAt format (ISO-8601 expected)");
        }
    }
}
