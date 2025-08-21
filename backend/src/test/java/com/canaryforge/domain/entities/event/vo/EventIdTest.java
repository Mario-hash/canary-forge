package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class EventIdTest {

    @Test
    @DisplayName("Constructor: value == null lanza IllegalArgumentException con mensaje específico")
    void constructor_null_throws() {
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> new EventId(null));
        assertEquals("EventId cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("newId(): genera UUID no nulo y distinto en llamadas consecutivas")
    void newId_generatesUnique() {
        EventId a = EventId.newId();
        EventId b = EventId.newId();

        assertNotNull(a.value());
        assertNotNull(b.value());
        assertNotEquals(a, b, "Dos newId() consecutivos deberían producir IDs distintos");
    }

    @Test
    @DisplayName("fromString(): con UUID válido crea EventId con ese UUID")
    void fromString_valid_ok() {
        UUID id = UUID.randomUUID();
        EventId eid = EventId.fromString(id.toString());

        assertEquals(id, eid.value());
        assertEquals(id.toString(), eid.toString());
    }

    @Test
    @DisplayName("fromString(): string inválido lanza IllegalArgumentException")
    void fromString_invalid_throws() {
        assertThrows(IllegalArgumentException.class, () -> EventId.fromString("not-a-uuid"));
    }

    @Test
    @DisplayName("fromString(): null lanza NullPointerException")
    void fromString_null_throws() {
        assertThrows(NullPointerException.class, () -> EventId.fromString(null));
    }

    @Test
    @DisplayName("equals/hashCode: dos EventId con el mismo UUID son iguales")
    void equals_hashCode_contract() {
        UUID id = UUID.randomUUID();
        EventId a = new EventId(id);
        EventId b = new EventId(id);
        EventId c = new EventId(UUID.randomUUID());

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}
