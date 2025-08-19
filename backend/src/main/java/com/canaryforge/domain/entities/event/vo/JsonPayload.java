package com.canaryforge.domain.entities.event.vo;

import com.canaryforge.domain.entities.event.exceptions.InvalidPayloadException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class JsonPayload {
    private static final int MAX_BYTES = 16 * 1024; // 16KB
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final String raw;

    private JsonPayload(String raw) {
        this.raw = raw;
    }

    public static JsonPayload of(String raw) {
        if (raw == null || raw.isBlank())
            return null;
        if (raw.getBytes().length > MAX_BYTES) {
            throw new InvalidPayloadException("payload too large (max " + MAX_BYTES + " bytes)");
        }
        try {
            MAPPER.readTree(raw);
        } catch (Exception e) {
            throw new InvalidPayloadException("payload must be valid JSON");
        }
        return new JsonPayload(raw);
    }

    public String raw() {
        return raw;
    }
}
