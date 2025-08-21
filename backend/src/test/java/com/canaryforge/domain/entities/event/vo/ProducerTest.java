package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.event.exceptions.InvalidProducerException;

class ProducerTest {

    @Test
    @DisplayName("Constructor: null o blank lanza InvalidProducerException('producer is required')")
    void constructor_nullOrBlank_throws() {
        InvalidProducerException ex1 = assertThrows(
                InvalidProducerException.class,
                () -> new Producer(null));
        assertEquals("producer is required", ex1.getMessage());

        InvalidProducerException ex2 = assertThrows(
                InvalidProducerException.class,
                () -> new Producer(""));
        assertEquals("producer is required", ex2.getMessage());

        InvalidProducerException ex3 = assertThrows(
                InvalidProducerException.class,
                () -> new Producer("   \t"));
        assertEquals("producer is required", ex3.getMessage());
    }

    @Test
    @DisplayName("Constructor: longitud > 100 lanza InvalidProducerException con el máximo en el mensaje")
    void constructor_tooLong_throws() {
        String tooLong = "a".repeat(101);
        InvalidProducerException ex = assertThrows(
                InvalidProducerException.class,
                () -> new Producer(tooLong));
        assertEquals("producer too long (max 100)", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: longitud mínima (1) y exactamente 100 son válidas")
    void constructor_minAndMax_ok() {
        Producer p1 = new Producer("a");
        assertEquals("a", p1.value());

        String hundred = "b".repeat(100);
        Producer p2 = new Producer(hundred);
        assertEquals(hundred, p2.value());
    }

    @Test
    @DisplayName("No trimea: valores con espacios finales se conservan si no son blank")
    void constructor_doesNotTrim_preservesValue() {
        Producer p = new Producer("abc ");
        assertEquals("abc ", p.value());
    }

    @Test
    @DisplayName("equals/hashCode de record: mismo valor => iguales; distinto => diferentes")
    void equals_hashCode_contract() {
        Producer a = new Producer("svc-A");
        Producer b = new Producer("svc-A");
        Producer c = new Producer("svc-B");

        assertEquals(a, b);
        assertEquals(a.hashCode(), b.hashCode());
        assertNotEquals(a, c);
    }
}
