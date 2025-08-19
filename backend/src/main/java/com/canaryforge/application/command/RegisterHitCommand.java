package com.canaryforge.application.command;

public record RegisterHitCommand(
        String sig,
        String userAgent,
        String referrer,
        String ipTrunc,
        String occurredAtIso,
        String eventType,
        String producer) {
}
