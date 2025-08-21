package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.event.exceptions.InvalidCorrelationIdException;

class CorrelationIdTest {

    @Test
    @DisplayName("Constructor: con UUID válido guarda el valor")
    void constructor_validUuid_storesValue() {
        UUID id = UUID.randomUUID();
        CorrelationId c = new CorrelationId(id);
        assertEquals(id, c.value());
    }

    @Test
    @DisplayName("Constructor: value == null lanza InvalidCorrelationIdException con mensaje específico")
    void constructor_null_throws() {
        InvalidCorrelationIdException ex = assertThrows(
                InvalidCorrelationIdException.class,
                () -> new CorrelationId(null));
        assertEquals("correlationId cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("fromString(): cadena UUID válida crea CorrelationId con ese UUID")
    void fromString_valid_ok() {
        UUID id = UUID.randomUUID();
        CorrelationId c = CorrelationId.fromString(id.toString());
        assertEquals(id, c.value());
    }

    @Test
    @DisplayName("fromString(): cadena inválida lanza InvalidCorrelationIdException con mensaje adecuado")
    void fromString_invalid_throws() {
        String bad = "not-a-uuid";
        InvalidCorrelationIdException ex = assertThrows(
                InvalidCorrelationIdException.class,
                () -> CorrelationId.fromString(bad));
        assertEquals("invalid correlationId (UUID expected)", ex.getMessage());
    }

    @Test
    @DisplayName("fromString(): null lanza InvalidCorrelationIdException con mensaje adecuado")
    void fromString_null_throws() {
        InvalidCorrelationIdException ex = assertThrows(
                InvalidCorrelationIdException.class,
                () -> CorrelationId.fromString(null));
        assertEquals("invalid correlationId (UUID expected)", ex.getMessage());
    }

    @Test
    @DisplayName("equals/hashCode: dos CorrelationId con el mismo UUID son iguales")
    void equals_hashCode_contract() {
        UUID id = UUID.randomUUID();
        CorrelationId a = new CorrelationId(id);
        CorrelationId b = new CorrelationId(id);
        CorrelationId c = new CorrelationId(UUID.randomUUID());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}
