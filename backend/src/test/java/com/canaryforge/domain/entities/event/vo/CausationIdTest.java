package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.event.exceptions.InvalidCausationIdException;

class CausationIdTest {

    @Test
    @DisplayName("Constructor: con UUID válido guarda el valor")
    void constructor_validUuid_storesValue() {
        UUID id = UUID.randomUUID();
        CausationId c = new CausationId(id);
        assertEquals(id, c.value());
    }

    @Test
    @DisplayName("Constructor: value == null lanza InvalidCausationIdException con mensaje específico")
    void constructor_null_throws() {
        InvalidCausationIdException ex = assertThrows(
                InvalidCausationIdException.class,
                () -> new CausationId(null));
        assertEquals("causationId cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("fromString(): cadena UUID válida crea CausationId con ese UUID")
    void fromString_valid_ok() {
        UUID id = UUID.randomUUID();
        CausationId c = CausationId.fromString(id.toString());
        assertEquals(id, c.value());
    }

    @Test
    @DisplayName("fromString(): cadena inválida lanza InvalidCausationIdException con mensaje adecuado")
    void fromString_invalid_throws() {
        String bad = "not-a-uuid";
        InvalidCausationIdException ex = assertThrows(
                InvalidCausationIdException.class,
                () -> CausationId.fromString(bad));
        assertEquals("invalid causationId (UUID expected)", ex.getMessage());
    }

    @Test
    @DisplayName("fromString(): null lanza InvalidCausationIdException con mensaje adecuado")
    void fromString_null_throws() {
        InvalidCausationIdException ex = assertThrows(
                InvalidCausationIdException.class,
                () -> CausationId.fromString(null));
        assertEquals("invalid causationId (UUID expected)", ex.getMessage());
    }

    @Test
    @DisplayName("equals/hashCode: dos CausationId con el mismo UUID son iguales")
    void equals_hashCode_contract() {
        UUID id = UUID.randomUUID();
        CausationId a = new CausationId(id);
        CausationId b = new CausationId(id);
        CausationId c = new CausationId(UUID.randomUUID());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}
