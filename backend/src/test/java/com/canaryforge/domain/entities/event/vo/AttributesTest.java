package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.event.exceptions.InvalidAttributesException;

class AttributesTest {

    @Test
    @DisplayName("of(null) y of(empty) devuelven Attributes.empty() (mapa vacío)")
    void of_nullOrEmpty_returnsEmpty() {
        Attributes a1 = Attributes.of(null);
        Attributes a2 = Attributes.of(Collections.emptyMap());
        Attributes empty = Attributes.empty();

        assertTrue(a1.value().isEmpty());
        assertTrue(a2.value().isEmpty());
        assertTrue(empty.value().isEmpty());
    }

    @Test
    @DisplayName("of(): permite hasta 20 entradas (máximo incluido)")
    void of_allowsUpToMaxEntries() {
        Map<String, String> m = new HashMap<>();
        for (int i = 1; i <= 20; i++) {
            m.put("k" + i, "v" + i);
        }
        Attributes attrs = Attributes.of(m);
        assertEquals(20, attrs.value().size());
    }

    @Test
    @DisplayName("of(): más de 20 entradas lanza InvalidAttributesException con mensaje adecuado")
    void of_moreThanMaxEntries_throws() {
        Map<String, String> m = new HashMap<>();
        for (int i = 1; i <= 21; i++) {
            m.put("k" + i, "v" + i);
        }
        InvalidAttributesException ex = assertThrows(
                InvalidAttributesException.class,
                () -> Attributes.of(m));
        assertEquals("too many attributes (max 20)", ex.getMessage());
    }

    @Test
    @DisplayName("of(): clave null lanza 'attribute key cannot be empty'")
    void of_nullKey_throws() {
        Map<String, String> m = new HashMap<>();
        m.put(null, "v");
        InvalidAttributesException ex = assertThrows(
                InvalidAttributesException.class,
                () -> Attributes.of(m));
        assertEquals("attribute key cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("of(): clave en blanco lanza 'attribute key cannot be empty'")
    void of_blankKey_throws() {
        Map<String, String> m = new HashMap<>();
        m.put("   \t", "v");
        InvalidAttributesException ex = assertThrows(
                InvalidAttributesException.class,
                () -> Attributes.of(m));
        assertEquals("attribute key cannot be empty", ex.getMessage());
    }

    @Test
    @DisplayName("of(): clave de longitud > 50 lanza mensaje con el máximo")
    void of_keyTooLong_throws() {
        String longKey = "a".repeat(51);
        Map<String, String> m = Map.of(longKey, "v");
        InvalidAttributesException ex = assertThrows(
                InvalidAttributesException.class,
                () -> Attributes.of(m));
        assertEquals("attribute key too long (max 50)", ex.getMessage());
    }

    @Test
    @DisplayName("of(): valor null es válido; valor exactamente 200 es válido")
    void of_valueNullAndMax_ok() {
        String v200 = "x".repeat(200);
        Map<String, String> m = new HashMap<>();
        m.put("k1", null);
        m.put("k2", v200);

        Attributes attrs = Attributes.of(m);
        assertNull(attrs.value().get("k1"));
        assertEquals(v200, attrs.value().get("k2"));
    }

    @Test
    @DisplayName("of(): valor de longitud > 200 lanza mensaje con el máximo")
    void of_valueTooLong_throws() {
        String tooLong = "y".repeat(201);
        Map<String, String> m = Map.of("k", tooLong);

        InvalidAttributesException ex = assertThrows(
                InvalidAttributesException.class,
                () -> Attributes.of(m));
        assertEquals("attribute value too long (max 200)", ex.getMessage());
    }

    @Test
    @DisplayName("value(): el mapa devuelto es inmodificable")
    void value_isUnmodifiable() {
        Map<String, String> m = Map.of("k", "v");
        Attributes attrs = Attributes.of(m);

        assertThrows(UnsupportedOperationException.class, () -> attrs.value().put("x", "y"));
        assertThrows(UnsupportedOperationException.class, () -> attrs.value().remove("k"));
        assertThrows(UnsupportedOperationException.class, () -> attrs.value().clear());

        // Y contiene los pares originales
        assertEquals("v", attrs.value().get("k"));
        assertEquals(1, attrs.value().size());
    }
}
