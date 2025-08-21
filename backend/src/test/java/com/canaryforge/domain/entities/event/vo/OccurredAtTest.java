package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Duration;
import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.event.exceptions.InvalidOccurredAtException;

class OccurredAtTest {

    @Test
    @DisplayName("Constructor: value == null lanza InvalidOccurredAtException('occurredAt is required')")
    void constructor_null_throws() {
        InvalidOccurredAtException ex = assertThrows(
                InvalidOccurredAtException.class,
                () -> new OccurredAt(null));
        assertEquals("occurredAt is required", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: instante pasado es válido")
    void constructor_past_ok() {
        Instant past = Instant.EPOCH; // 1970-01-01
        OccurredAt oa = new OccurredAt(past);
        assertEquals(past, oa.value());
    }

    @Test
    @DisplayName("Constructor: futuro dentro de tolerancia (~4 min) es válido")
    void constructor_futureWithinTolerance_ok() {
        Instant within = Instant.now().plus(Duration.ofMinutes(4));
        OccurredAt oa = new OccurredAt(within);
        assertEquals(within, oa.value());
    }

    @Test
    @DisplayName("Constructor: futuro más allá de tolerancia (~6 min) lanza excepción")
    void constructor_futureBeyondTolerance_throws() {
        Instant beyond = Instant.now().plus(Duration.ofMinutes(6));
        InvalidOccurredAtException ex = assertThrows(
                InvalidOccurredAtException.class,
                () -> new OccurredAt(beyond));
        assertEquals("occurredAt is too far in the future", ex.getMessage());
    }

    @Test
    @DisplayName("parseIso(): ISO-8601 válido crea OccurredAt con el mismo instante")
    void parseIso_valid_ok() {
        Instant ts = Instant.parse("2023-01-01T10:15:30Z");
        OccurredAt oa = OccurredAt.parseIso("2023-01-01T10:15:30Z");
        assertEquals(ts, oa.value());
    }

    @Test
    @DisplayName("parseIso(): formato inválido lanza InvalidOccurredAtException con mensaje adecuado")
    void parseIso_invalid_throws() {
        InvalidOccurredAtException ex = assertThrows(
                InvalidOccurredAtException.class,
                () -> OccurredAt.parseIso("not-an-instant"));
        assertEquals("invalid occurredAt format (ISO-8601 expected)", ex.getMessage());
    }

    @Test
    @DisplayName("parseIso(): null lanza InvalidOccurredAtException con mensaje adecuado")
    void parseIso_null_throws() {
        InvalidOccurredAtException ex = assertThrows(
                InvalidOccurredAtException.class,
                () -> OccurredAt.parseIso(null));
        assertEquals("invalid occurredAt format (ISO-8601 expected)", ex.getMessage());
    }

    @Test
    @DisplayName("equals/hashCode (record) y value()")
    void equals_hash_value_contract() {
        Instant when = Instant.parse("2022-06-01T00:00:00Z");
        OccurredAt a = new OccurredAt(when);
        OccurredAt b = new OccurredAt(when);
        OccurredAt c = new OccurredAt(when.plusSeconds(1));

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
        assertEquals(when, a.value());
    }
}
