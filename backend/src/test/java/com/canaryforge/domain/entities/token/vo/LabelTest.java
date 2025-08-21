package com.canaryforge.domain.entities.token.vo;

import com.canaryforge.domain.entities.token.exceptions.vo.InvalidLabelException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LabelTest {

    @ParameterizedTest
    @DisplayName("of() acepta valores válidos que cumplen el patrón y longitud")
    @ValueSource(strings = {
            "a",
            "A_B-9",
            "abc123_-XYZ",
    })
    void of_accepts_valid_values(String raw) {
        Label l = Label.of(raw);
        assertThat(l.value()).isEqualTo(raw);
        assertThat(l.toString()).isEqualTo(raw);
    }

    @Test
    @DisplayName("of() acepta longitud exactamente 60 (borde superior)")
    void of_accepts_length_60() {
        String sixty = "a".repeat(60);
        Label l = Label.of(sixty);
        assertThat(l.value()).isEqualTo(sixty);
        assertThat(sixty.length()).isEqualTo(60);
    }

    @Test
    @DisplayName("of() lanza si raw es null")
    void of_throws_on_null() {
        assertThrows(InvalidLabelException.class, () -> Label.of(null));
    }

    @ParameterizedTest
    @DisplayName("of() lanza si raw es blanco o vacío")
    @ValueSource(strings = { "", " ", "   ", "\t", "\n" })
    void of_throws_on_blank(String raw) {
        assertThrows(InvalidLabelException.class, () -> Label.of(raw));
    }

    @Test
    @DisplayName("of() lanza si longitud > 60")
    void of_throws_on_too_long() {
        String tooLong = "a".repeat(61);
        assertThrows(InvalidLabelException.class, () -> Label.of(tooLong));
    }

    @ParameterizedTest
    @DisplayName("of() lanza si no cumple el regex permitido")
    @ValueSource(strings = {
            "abc!",
            "with space",
            "áéíóú",
            "slash/",
            "dot.",
            "coma,",
    })
    void of_throws_on_invalid_regex(String raw) {
        assertThrows(InvalidLabelException.class, () -> Label.of(raw));
    }

    @Test
    @DisplayName("equals/hashCode: igualdad por valor, no por instancia")
    void equals_and_hashCode_by_value() {
        Label a1 = Label.of("cv_label");
        Label a2 = Label.of("cv_label");
        Label b = Label.of("other");

        assertThat(a1).isEqualTo(a1);
        assertThat(a1).isEqualTo(a2).hasSameHashCodeAs(a2);

        assertThat(a1).isNotEqualTo(b);

        assertThat(a1).isNotEqualTo(null);
        assertThat(a1).isNotEqualTo("cv_label");
    }
}
