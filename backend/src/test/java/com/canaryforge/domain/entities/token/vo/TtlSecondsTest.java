package com.canaryforge.domain.entities.token.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.token.exceptions.vo.InvalidTtlException;

class TtlSecondsTest {

    @Test
    @DisplayName("of(): valor válido dentro del rango crea TtlSeconds")
    void of_validValue_returnsTtlSeconds() {
        TtlSeconds ttl = TtlSeconds.of(120);
        assertNotNull(ttl);
        assertEquals(120, ttl.value());
    }

    @Test
    @DisplayName("of(): exactamente MIN (60) es válido")
    void of_minBoundary_ok() {
        TtlSeconds ttl = TtlSeconds.of(TtlSeconds.MIN);
        assertEquals(60, ttl.value());
    }

    @Test
    @DisplayName("of(): exactamente MAX (2592000) es válido")
    void of_maxBoundary_ok() {
        TtlSeconds ttl = TtlSeconds.of(TtlSeconds.MAX);
        assertEquals(2_592_000, ttl.value());
    }

    @Test
    @DisplayName("of(): menor que MIN lanza InvalidTtlException")
    void of_belowMin_throws() {
        InvalidTtlException ex = assertThrows(
                InvalidTtlException.class,
                () -> TtlSeconds.of(TtlSeconds.MIN - 1));
        assertEquals("ttlSec out of range (60..2592000)", ex.getMessage());
    }

    @Test
    @DisplayName("of(): mayor que MAX lanza InvalidTtlException")
    void of_aboveMax_throws() {
        InvalidTtlException ex = assertThrows(
                InvalidTtlException.class,
                () -> TtlSeconds.of(TtlSeconds.MAX + 1));
        assertEquals("ttlSec out of range (60..2592000)", ex.getMessage());
    }
}
