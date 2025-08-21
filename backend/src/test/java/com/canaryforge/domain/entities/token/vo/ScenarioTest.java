package com.canaryforge.domain.entities.token.vo;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.token.exceptions.vo.InvalidScenarioException;

class ScenarioTest {

    @Test
    @DisplayName("of(): valor válido retorna Scenario con el mismo valor")
    void of_valid_returnsScenario() {
        Scenario s = Scenario.of("Checkout_v1-OK");
        assertNotNull(s);
        assertEquals("Checkout_v1-OK", s.value());
        assertEquals("Checkout_v1-OK", s.toString());
    }

    @Test
    @DisplayName("of(): null y blank lanzan InvalidScenarioException con mensaje 'scenario is blank'")
    void of_nullOrBlank_throws() {
        assertEquals("scenario is blank",
                assertThrows(InvalidScenarioException.class, () -> Scenario.of(null)).getMessage());
        assertEquals("scenario is blank",
                assertThrows(InvalidScenarioException.class, () -> Scenario.of("")).getMessage());
        assertEquals("scenario is blank",
                assertThrows(InvalidScenarioException.class, () -> Scenario.of("   ")).getMessage());
    }

    @Test
    @DisplayName("of(): longitud > 60 lanza InvalidScenarioException con mensaje 'scenario too long'")
    void of_tooLong_throws() {
        String sixtyOne = "a".repeat(61);
        InvalidScenarioException ex = assertThrows(InvalidScenarioException.class, () -> Scenario.of(sixtyOne));
        assertEquals("scenario too long", ex.getMessage());
    }

    @Test
    @DisplayName("of(): longitud exactamente 60 es válida")
    void of_exactlySixty_ok() {
        String sixty = "a".repeat(60);
        Scenario s = Scenario.of(sixty);
        assertEquals(sixty, s.value());
    }

    @Test
    @DisplayName("of(): caracteres no permitidos (espacios, acentos, punto) lanzan excepción")
    void of_invalidRegex_throws() {
        assertEquals("scenario must match ^[A-Za-z0-9_-]{1,60}$",
                assertThrows(InvalidScenarioException.class, () -> Scenario.of("bad value")).getMessage());
        assertEquals("scenario must match ^[A-Za-z0-9_-]{1,60}$",
                assertThrows(InvalidScenarioException.class, () -> Scenario.of("bad.value")).getMessage());
        assertEquals("scenario must match ^[A-Za-z0-9_-]{1,60}$",
                assertThrows(InvalidScenarioException.class, () -> Scenario.of("málaga")).getMessage());
    }

    @Test
    @DisplayName("of(): acepta guion y guion bajo")
    void of_acceptsHyphenAndUnderscore() {
        Scenario a = Scenario.of("abc_def");
        Scenario b = Scenario.of("abc-def");
        assertEquals("abc_def", a.value());
        assertEquals("abc-def", b.value());
    }

    @Test
    @DisplayName("equals/hashCode: mismo valor => iguales; distinto valor => diferentes")
    void equals_hashCode_contract() {
        Scenario s1 = Scenario.of("ScenarioX");
        Scenario s2 = Scenario.of("ScenarioX");
        Scenario s3 = Scenario.of("ScenarioY");

        assertEquals(s1, s2);
        assertEquals(s1.hashCode(), s2.hashCode());
        assertNotEquals(s1, s3);
    }

    @Test
    @DisplayName("equals: null y otro tipo => false")
    void equals_withNullAndDifferentType() {
        Scenario s = Scenario.of("Z");
        assertNotEquals(null, s);
        assertNotEquals(s, "Z");
    }

    @Test
    @DisplayName("of(): conserva valor permitido sin trim (ej: guion bajo final)")
    void of_preservesValue_withoutTrimming() {
        String raw = "abc_";
        Scenario s = Scenario.of(raw);
        assertEquals(raw, s.value());
    }

    @Test
    @DisplayName("of(): valor con espacio final no permitido lanza excepción")
    void of_withSpace_throws() {
        String raw = "abc ";
        assertThrows(InvalidScenarioException.class, () -> Scenario.of(raw));
    }
}
