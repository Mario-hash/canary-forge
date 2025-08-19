package com.canaryforge.adapter.web.dto;

import java.time.Instant;
import java.util.Map;

public record EventSseDto(
        String id,
        String type,
        Instant occurredAt,
        String producer,
        int version,
        Map<String, String> attributes) {
}
