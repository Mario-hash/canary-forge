package com.canaryforge.domain.entities.token.vo;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Instant;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.token.exceptions.InvalidExpirationException;

class TokenClaimsTest {

    private static final TokenType ANY_TYPE = TokenType.PIX; // Ajusta si tu enum usa otro nombre
    private static final Label ANY_LABEL = Label.of("Label_1");
    private static final Scenario ANY_SCENARIO = Scenario.of("Scenario_1");
    private static final Version ANY_VERSION = Version.of(1);
    private static final Nonce ANY_REQUEST_ID = Nonce.of("req-123");
    private static final Instant FUTURE_EXP = Instant.ofEpochSecond(2_000_000_000L); // ~2033

    @Test
    @DisplayName("of(): crea correctamente TokenClaims con valores válidos")
    void of_createsValidTokenClaims() {
        TokenClaims tc = TokenClaims.of(
                ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);

        assertNotNull(tc);
        assertEquals(ANY_TYPE, tc.tokenType());
        assertEquals(ANY_LABEL, tc.label());
        assertEquals(ANY_SCENARIO, tc.scenario());
        assertEquals(ANY_VERSION, tc.version());
        assertEquals(ANY_REQUEST_ID, tc.requestId());
        assertEquals(FUTURE_EXP, tc.expiration());
    }

    @Test
    @DisplayName("ofEpochSeconds(): crea correctamente usando epoch seconds")
    void ofEpochSeconds_createsFromEpoch() {
        long exp = 2_000_000_100L; // futuro también
        TokenClaims tc = TokenClaims.ofEpochSeconds(
                ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, exp);

        assertEquals(Instant.ofEpochSecond(exp), tc.expiration());
    }

    @Test
    @DisplayName("toString(): contiene todos los fragmentos esperados")
    void toString_containsExpectedParts() {
        TokenClaims tc = TokenClaims.of(
                ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);

        String s = tc.toString();
        assertTrue(s.startsWith("TokenClaims["), "Debe empezar por TokenClaims[");
        assertTrue(s.contains(ANY_TYPE.toString()), "Debe contener el tokenType");
        assertTrue(s.contains(ANY_LABEL.toString()), "Debe contener el label");
        assertTrue(s.contains(ANY_SCENARIO.toString()), "Debe contener el scenario");
        assertTrue(s.contains("v=" + ANY_VERSION.value()), "Debe contener v=<version.value()>");
        assertTrue(s.contains("r=" + ANY_REQUEST_ID.value()), "Debe contener r=<requestId.value()>");
        assertTrue(s.contains("exp=" + FUTURE_EXP), "Debe contener exp=<expiration>");
        assertTrue(s.endsWith("]"), "Debe terminar con ]");
    }

    @Test
    @DisplayName("Constructor: tokenType null lanza NPE con mensaje 'tokenType'")
    void ctor_null_tokenType_throws() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> TokenClaims.of(null, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP));
        assertEquals("tokenType", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: label null lanza NPE con mensaje 'label'")
    void ctor_null_label_throws() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> TokenClaims.of(ANY_TYPE, null, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP));
        assertEquals("label", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: scenario null lanza NPE con mensaje 'scenario'")
    void ctor_null_scenario_throws() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> TokenClaims.of(ANY_TYPE, ANY_LABEL, null, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP));
        assertEquals("scenario", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: version null lanza NPE con mensaje 'version'")
    void ctor_null_version_throws() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, null, ANY_REQUEST_ID, FUTURE_EXP));
        assertEquals("version", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: requestId null lanza NPE con mensaje 'requestId'")
    void ctor_null_requestId_throws() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, null, FUTURE_EXP));
        assertEquals("requestId", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: expiration null lanza NPE con mensaje 'expiration'")
    void ctor_null_expiration_throws() {
        NullPointerException ex = assertThrows(NullPointerException.class,
                () -> TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, null));
        assertEquals("expiration", ex.getMessage());
    }

    @Test
    @DisplayName("Constructor: expiration en el pasado lanza InvalidExpirationException('token expired')")
    void ctor_pastExpiration_throws() {
        Instant past = Instant.now().minusSeconds(1);
        InvalidExpirationException ex = assertThrows(InvalidExpirationException.class,
                () -> TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, past));
        assertEquals("token expired", ex.getMessage());
    }

    @Test
    @DisplayName("equals/hashCode: objetos idénticos son iguales y tienen mismo hash")
    void equals_hash_sameObjects() {
        TokenClaims a = TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);
        TokenClaims b = TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);

        assertEquals(a, a); // reflexividad
        assertEquals(a, b);
        assertEquals(b, a);
        assertEquals(a.hashCode(), b.hashCode());
    }

    @Test
    @DisplayName("equals/hashCode: difiere si cambia cualquier campo")
    void equals_differsByAnyField() {
        TokenClaims base = TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);

        TokenClaims diffType = TokenClaims.of(
                anotherType(ANY_TYPE), ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);
        assertNotEquals(base, diffType);

        TokenClaims diffLabel = TokenClaims.of(
                ANY_TYPE, Label.of("Other"), ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);
        assertNotEquals(base, diffLabel);

        TokenClaims diffScenario = TokenClaims.of(
                ANY_TYPE, ANY_LABEL, Scenario.of("OtherScenario"), ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);
        assertNotEquals(base, diffScenario);

        TokenClaims diffVersion = TokenClaims.of(
                ANY_TYPE, ANY_LABEL, ANY_SCENARIO, Version.of(2), ANY_REQUEST_ID, FUTURE_EXP);
        assertNotEquals(base, diffVersion);

        TokenClaims diffReq = TokenClaims.of(
                ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, Nonce.of("req-999"), FUTURE_EXP);
        assertNotEquals(base, diffReq);

        TokenClaims diffExp = TokenClaims.of(
                ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP.plusSeconds(60));
        assertNotEquals(base, diffExp);
    }

    @Test
    @DisplayName("equals: null y otro tipo => false")
    void equals_nullAndDifferentType() {
        TokenClaims tc = TokenClaims.of(ANY_TYPE, ANY_LABEL, ANY_SCENARIO, ANY_VERSION, ANY_REQUEST_ID, FUTURE_EXP);
        assertNotEquals(tc, null);
        assertNotEquals(null, tc);
        assertNotEquals(tc, "string");
    }

    // Helper para obtener otro valor del enum sin acoplar al número de valores
    private static TokenType anotherType(TokenType current) {
        for (TokenType t : TokenType.values()) {
            if (t != current)
                return t;
        }
        // Si solo hay un valor en el enum, usamos el propio (el test seguirá pasando al
        // cubrir otras diferencias)
        return current;
    }
}
