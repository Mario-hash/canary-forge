package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.event.exceptions.InvalidEventTypeException;

class EventTypeTest {

    @Test
    @DisplayName("from(): acepta valores válidos en cualquier case y con espacios alrededor")
    void from_acceptsValidValues_anyCase_andTrimmed() {
        // CLICK
        assertEquals(EventType.CLICK, EventType.from("CLICK"));
        assertEquals(EventType.CLICK, EventType.from("click"));
        assertEquals(EventType.CLICK, EventType.from("  Click  "));

        // VIEW
        assertEquals(EventType.VIEW, EventType.from("VIEW"));
        assertEquals(EventType.VIEW, EventType.from("view"));
        assertEquals(EventType.VIEW, EventType.from("\tView\n"));

        // TOKEN_CREATED
        assertEquals(EventType.TOKEN_CREATED, EventType.from("TOKEN_CREATED"));
        assertEquals(EventType.TOKEN_CREATED, EventType.from("token_created"));
        assertEquals(EventType.TOKEN_CREATED, EventType.from("  ToKeN_cReAtEd "));

        // TOKEN_USED
        assertEquals(EventType.TOKEN_USED, EventType.from("TOKEN_USED"));
        assertEquals(EventType.TOKEN_USED, EventType.from("token_used"));

        // ERROR
        assertEquals(EventType.ERROR, EventType.from("ERROR"));
        assertEquals(EventType.ERROR, EventType.from("error"));

        // CUSTOM
        assertEquals(EventType.CUSTOM, EventType.from("CUSTOM"));
        assertEquals(EventType.CUSTOM, EventType.from("custom"));
    }

    @Test
    @DisplayName("from(): null o blank lanza InvalidEventTypeException con 'event type is required'")
    void from_nullOrBlank_throws() {
        InvalidEventTypeException ex1 = assertThrows(
                InvalidEventTypeException.class,
                () -> EventType.from(null));
        assertEquals("event type is required", ex1.getMessage());

        InvalidEventTypeException ex2 = assertThrows(
                InvalidEventTypeException.class,
                () -> EventType.from(""));
        assertEquals("event type is required", ex2.getMessage());

        InvalidEventTypeException ex3 = assertThrows(
                InvalidEventTypeException.class,
                () -> EventType.from("   \t"));
        assertEquals("event type is required", ex3.getMessage());
    }

    @Test
    @DisplayName("from(): valor no soportado incluye el raw en el mensaje")
    void from_unsupportedValue_throws() {
        String raw = "opened";
        InvalidEventTypeException ex = assertThrows(
                InvalidEventTypeException.class,
                () -> EventType.from(raw));
        assertEquals("unsupported event type: " + raw, ex.getMessage());
    }

    @Test
    @DisplayName("Enum contiene las constantes esperadas en el orden declarado")
    void enum_hasExpectedConstants() {
        assertArrayEquals(
                new EventType[] {
                        EventType.CLICK,
                        EventType.VIEW,
                        EventType.TOKEN_CREATED,
                        EventType.TOKEN_USED,
                        EventType.ERROR,
                        EventType.CUSTOM
                },
                EventType.values());
    }
}
