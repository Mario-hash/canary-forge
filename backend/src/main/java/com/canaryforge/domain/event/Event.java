package com.canaryforge.domain.event;

import java.time.Instant;

public record Event(
        String id,
        String type, // "HIT" | "USED"
        String tokenType, // "URL" | "PIX" | "KEY"
        String label,
        String scenario,
        Source source,
        Severity severity,
        Instant createdAt) {
    public record Source(String ipTrunc, String ua, String referrer) {
    }
}
