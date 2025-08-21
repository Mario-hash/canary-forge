package com.canaryforge.domain.entities.token.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class NonceTest {

    @Test
    @DisplayName("of() con valor válido devuelve Nonce con el mismo valor")
    void of_withValidValue_returnsNonce() {
        String raw = "abc-123";
        Nonce n = Nonce.of(raw);

        assertNotNull(n);
        assertEquals(raw, n.value());
        assertEquals(raw, n.toString(), "toString() debe devolver exactamente el valor interno");
    }

    @Test
    @DisplayName("of() lanza IllegalArgumentException cuando el valor es null")
    void of_withNull_throws() {
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> Nonce.of(null));
        assertEquals("nonce is blank", ex.getMessage());
    }

    @Test
    @DisplayName("of() lanza IllegalArgumentException cuando el valor es vacío o en blanco")
    void of_withBlank_throws() {
        IllegalArgumentException ex1 = assertThrows(IllegalArgumentException.class, () -> Nonce.of(""));
        assertEquals("nonce is blank", ex1.getMessage());

        IllegalArgumentException ex2 = assertThrows(IllegalArgumentException.class, () -> Nonce.of("   \t"));
        assertEquals("nonce is blank", ex2.getMessage());
    }

    @Test
    @DisplayName("random() genera valores no en blanco y distintos entre sí")
    void random_generatesNonBlankAndDifferentValues() {
        Nonce n1 = Nonce.random();
        Nonce n2 = Nonce.random();

        assertNotNull(n1);
        assertNotNull(n2);
        assertFalse(n1.value().isBlank(), "random() no debe generar blancos");
        assertFalse(n2.value().isBlank(), "random() no debe generar blancos");
        assertNotEquals(n1.value(), n2.value(),
                "Dos random() consecutivos deberían diferir la inmensa mayoría de veces");
    }

    @Test
    @DisplayName("equals/hashCode: mismo valor => iguales y mismo hash; distinto valor => diferentes")
    void equalsAndHashCode_contract() {
        Nonce a1 = Nonce.of("same");
        Nonce a2 = Nonce.of("same");
        Nonce b = Nonce.of("other");

        // Reflexividad
        assertEquals(a1, a1);

        // Simetría y consistencia
        assertEquals(a1, a2);
        assertEquals(a2, a1);
        assertEquals(a1.hashCode(), a2.hashCode(), "hashCode debe coincidir para objetos iguales");

        // Desigualdad
        assertNotEquals(a1, b);
        assertNotEquals(b, a1);
        assertNotEquals(a1.hashCode(), b.hashCode(),
                "hashCodes suelen diferir para objetos distintos (no es requisito, pero aquí debería)");
    }

    @Test
    @DisplayName("equals con null y con otro tipo devuelve false")
    void equals_withNullAndDifferentType_returnsFalse() {
        Nonce n = Nonce.of("x");
        assertNotEquals(null, n);
        assertNotEquals(n, null);
        assertNotEquals(n, "x"); // objeto de distinto tipo
    }

    @Test
    @DisplayName("of() no trimea: conserva espacios si no es blanco")
    void of_preservesValue() {
        String raw = "abc ";
        Nonce n = Nonce.of(raw);
        assertEquals(raw, n.value(), "Debe conservar el valor tal cual fue recibido");
    }
}
