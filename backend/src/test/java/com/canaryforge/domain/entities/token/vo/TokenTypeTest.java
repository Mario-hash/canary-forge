package com.canaryforge.domain.entities.token.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.token.exceptions.UnsupportedTokenTypeException;

class TokenTypeTest {

    @Test
    @DisplayName("from(): acepta valores válidos en cualquier case")
    void from_acceptsValidValuesAnyCase() {
        assertEquals(TokenType.URL, TokenType.from("URL"));
        assertEquals(TokenType.URL, TokenType.from("url"));
        assertEquals(TokenType.URL, TokenType.from("UrL"));

        assertEquals(TokenType.PIX, TokenType.from("PIX"));
        assertEquals(TokenType.PIX, TokenType.from("pix"));
        assertEquals(TokenType.PIX, TokenType.from("PiX"));

        assertEquals(TokenType.KEY, TokenType.from("KEY"));
        assertEquals(TokenType.KEY, TokenType.from("key"));
        assertEquals(TokenType.KEY, TokenType.from("kEy"));
    }

    @Test
    @DisplayName("from(): null lanza UnsupportedTokenTypeException con mensaje específico")
    void from_null_throws() {
        UnsupportedTokenTypeException ex = assertThrows(
                UnsupportedTokenTypeException.class,
                () -> TokenType.from(null));
        assertEquals("TokenType cannot be null", ex.getMessage());
    }

    @Test
    @DisplayName("from(): valor no soportado lanza UnsupportedTokenTypeException con el valor en el mensaje")
    void from_unsupportedValue_throws() {
        String bad = "qr";
        UnsupportedTokenTypeException ex = assertThrows(
                UnsupportedTokenTypeException.class,
                () -> TokenType.from(bad));
        assertEquals("Unsupported TokenType: " + bad, ex.getMessage());
    }

    @Test
    @DisplayName("from(): no trimea -> valores con espacios se consideran no soportados")
    void from_doesNotTrim() {
        String spaced = " url ";
        UnsupportedTokenTypeException ex = assertThrows(
                UnsupportedTokenTypeException.class,
                () -> TokenType.from(spaced));
        assertEquals("Unsupported TokenType: " + spaced, ex.getMessage());
    }

    @Test
    @DisplayName("Enum contiene las constantes esperadas")
    void enum_hasExpectedConstants() {
        assertArrayEquals(
                new TokenType[] { TokenType.URL, TokenType.PIX, TokenType.KEY },
                TokenType.values());
    }
}
