package com.canaryforge.domain.entities.event.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.event.exceptions.InvalidPayloadException;

class JsonPayloadTest {

    private static final int MAX_BYTES = 16 * 1024; // 16384

    @Test
    @DisplayName("of(): null o blank devuelve null")
    void of_nullOrBlank_returnsNull() {
        assertNull(JsonPayload.of(null));
        assertNull(JsonPayload.of(""));
        assertNull(JsonPayload.of("   \t"));
    }

    @Test
    @DisplayName("of(): JSON válido devuelve instancia con raw idéntico")
    void of_validJson_returnsInstance() {
        String json = "{\"a\":1,\"b\":[true,false],\"c\":{\"d\":\"x\"}}";
        JsonPayload p = JsonPayload.of(json);

        assertNotNull(p);
        assertEquals(json, p.raw());
    }

    @Test
    @DisplayName("of(): tamaño exactamente 16KB es válido")
    void of_exactMaxBytes_ok() {
        // Plantilla {"a":""} son 8 chars; completamos con 'x' ASCII para que
        // chars==bytes
        int overhead = 8;
        int n = MAX_BYTES - overhead; // 16376
        String big = "{\"a\":\"" + "x".repeat(n) + "\"}";

        assertEquals(MAX_BYTES, big.getBytes().length, "Precaución: el test debe generar 16KB exactos");
        JsonPayload p = JsonPayload.of(big);
        assertNotNull(p);
        assertEquals(big, p.raw());
    }

    @Test
    @DisplayName("of(): tamaño > 16KB lanza InvalidPayloadException con mensaje")
    void of_aboveMax_throws() {
        int overhead = 8;
        int n = (MAX_BYTES - overhead) + 1; // 16KB + 1
        String tooBig = "{\"a\":\"" + "x".repeat(n) + "\"}";

        InvalidPayloadException ex = assertThrows(
                InvalidPayloadException.class,
                () -> JsonPayload.of(tooBig));
        assertEquals("payload too large (max " + MAX_BYTES + " bytes)", ex.getMessage());
    }

    @Test
    @DisplayName("of(): JSON inválido lanza InvalidPayloadException con mensaje")
    void of_invalidJson_throws() {
        String notJson = "{not json}";
        InvalidPayloadException ex = assertThrows(
                InvalidPayloadException.class,
                () -> JsonPayload.of(notJson));
        assertEquals("payload must be valid JSON", ex.getMessage());
    }
}
