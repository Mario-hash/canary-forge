package com.canaryforge.domain.entities.token;

import static org.junit.jupiter.api.Assertions.*;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.token.exceptions.InvalidExpirationException;
import com.canaryforge.domain.entities.token.exceptions.UnsupportedTokenTypeException;
import com.canaryforge.domain.entities.token.vo.Label;
import com.canaryforge.domain.entities.token.vo.Scenario;
import com.canaryforge.domain.entities.token.vo.TokenType;
import com.canaryforge.domain.entities.token.vo.TtlSeconds;

class TokenTest {

    private static final ZoneId UTC = ZoneId.of("UTC");

    @Test
    @DisplayName("create(): genera Token válido con expiración = now + ttl y version v1")
    void create_validToken_ok() {
        Instant base = Instant.parse("2030-01-01T00:00:00Z");
        Clock fixed = Clock.fixed(base, UTC);

        Token t = Token.create(
                TokenType.URL,
                Label.of("L1"),
                Scenario.of("S1"),
                TtlSeconds.of(120),
                fixed);

        assertNotNull(t);
        assertEquals(TokenType.URL, t.type());
        assertEquals("L1", t.label().value());
        assertEquals("S1", t.scenario().value());
        assertEquals(120, t.ttl().value());
        assertEquals(1, t.version().value(), "Version.v1() debe devolver 1");
        assertEquals(base.plusSeconds(120), t.exp());
        assertNotNull(t.nonce().value());
        assertFalse(t.nonce().value().isBlank());
    }

    @Test
    @DisplayName("toPayload(): devuelve todas las claves con los valores esperados")
    void toPayload_containsExpectedKeysAndValues() {
        Instant base = Instant.parse("2030-01-01T12:00:00Z");
        Clock fixed = Clock.fixed(base, UTC);

        Token t = Token.create(
                TokenType.PIX,
                Label.of("Checkout"),
                Scenario.of("HappyPath"),
                TtlSeconds.of(300),
                fixed);

        Map<String, Object> p = t.toPayload();

        assertEquals(6, p.size());
        assertEquals("PIX", p.get("t"));
        assertEquals("Checkout", p.get("label"));
        assertEquals("HappyPath", p.get("sc"));
        assertEquals(t.exp().getEpochSecond(), p.get("exp"));
        assertEquals(t.version().value(), p.get("v"));
        assertEquals(t.nonce().value(), p.get("r"));
    }

    @Test
    @DisplayName("create(): type == null lanza UnsupportedTokenTypeException con mensaje 'type is null'")
    void create_nullType_throws() {
        Clock fixed = Clock.fixed(Instant.parse("2035-05-05T05:05:05Z"), UTC);

        UnsupportedTokenTypeException ex = assertThrows(
                UnsupportedTokenTypeException.class,
                () -> Token.create(
                        null,
                        Label.of("L"),
                        Scenario.of("S"),
                        TtlSeconds.of(120),
                        fixed));
        assertEquals("type is null", ex.getMessage());
    }

    @Test
    @DisplayName("create(): exp antes de now+30 lanza InvalidExpirationException (forzado con Clock que avanza)")
    void create_expNotInFuture_throws() {
        Instant t0 = Instant.parse("2040-01-01T00:00:00Z");
        Instant t1 = t0.plusSeconds(1000); // la segunda llamada a now(clock) será muy posterior

        Clock jumpingClock = new JumpingClock(t0, t1, UTC);

        InvalidExpirationException ex = assertThrows(
                InvalidExpirationException.class,
                () -> Token.create(
                        TokenType.KEY,
                        Label.of("Lbl"),
                        Scenario.of("Scn"),
                        TtlSeconds.of(60), // exp = t0 + 60
                        jumpingClock // comparación se hace contra t1 + 30, por lo que exp < t1+30
                ));
        assertEquals("computed exp is not in the future", ex.getMessage());
    }

    @Test
    @DisplayName("equals/hashCode: reflexividad y desigualdad por nonce aleatorio y por cambiar campos")
    void equals_hashCode_behavior() {
        Instant base = Instant.parse("2050-06-01T00:00:00Z");
        Clock fixed = Clock.fixed(base, UTC);

        Token a = Token.create(TokenType.URL, Label.of("L"), Scenario.of("S"), TtlSeconds.of(120), fixed);
        Token b = Token.create(TokenType.URL, Label.of("L"), Scenario.of("S"), TtlSeconds.of(120), fixed);
        Token c = Token.create(TokenType.PIX, Label.of("L"), Scenario.of("S"), TtlSeconds.of(120), fixed);

        // Reflexividad
        assertEquals(a, a);
        assertEquals(a.hashCode(), a.hashCode());

        // Como el nonce es aleatorio, dos tokens creados igual NO deberían ser iguales
        assertNotEquals(a, b);

        // Cambiar el tipo debe implicar desigualdad
        assertNotEquals(a, c);
    }

    /**
     * Clock que devuelve el primer Instant en la primera llamada y el segundo
     * Instant en la segunda (y sucesivas).
     */
    private static final class JumpingClock extends Clock {
        private final Instant first;
        private final Instant thereafter;
        private final ZoneId zone;
        private final AtomicInteger calls = new AtomicInteger();

        private JumpingClock(Instant first, Instant thereafter, ZoneId zone) {
            this.first = first;
            this.thereafter = thereafter;
            this.zone = zone;
        }

        @Override
        public ZoneId getZone() {
            return zone;
        }

        @Override
        public Clock withZone(ZoneId zone) {
            return new JumpingClock(first, thereafter, zone);
        }

        @Override
        public Instant instant() {
            return calls.getAndIncrement() == 0 ? first : thereafter;
        }
    }
}
