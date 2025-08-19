package com.canaryforge.domain.entities.token;

import java.time.Clock;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

import com.canaryforge.domain.entities.token.exceptions.InvalidExpirationException;
import com.canaryforge.domain.entities.token.exceptions.UnsupportedTokenTypeException;
import com.canaryforge.domain.entities.token.vo.Label;
import com.canaryforge.domain.entities.token.vo.Nonce;
import com.canaryforge.domain.entities.token.vo.Scenario;
import com.canaryforge.domain.entities.token.vo.TokenType;
import com.canaryforge.domain.entities.token.vo.TtlSeconds;
import com.canaryforge.domain.entities.token.vo.Version;

public final class Token {
    private final TokenType type;
    private final Label label;
    private final Scenario scenario;
    private final TtlSeconds ttl;
    private final Version version;
    private final Nonce nonce;
    private final Instant exp;

    private Token(TokenType type, Label label, Scenario scenario, TtlSeconds ttl,
            Version version, Nonce nonce, Instant exp) {
        this.type = type;
        this.label = label;
        this.scenario = scenario;
        this.ttl = ttl;
        this.version = version;
        this.nonce = nonce;
        this.exp = exp;
    }

    public static Token create(TokenType type, Label label, Scenario scenario, TtlSeconds ttl, Clock clock) {
        if (type == null)
            throw new UnsupportedTokenTypeException("type is null");
        Instant exp = Instant.now(clock).plusSeconds(ttl.value());
        if (exp.isBefore(Instant.now(clock).plusSeconds(30))) {
            throw new InvalidExpirationException("computed exp is not in the future");
        }
        return new Token(type, label, scenario, ttl, Version.v1(), Nonce.random(), exp);
    }

    public Map<String, Object> toPayload() {
        Map<String, Object> p = new LinkedHashMap<>();
        p.put("t", type.name()); // tipo
        p.put("label", label.value()); // etiqueta
        p.put("sc", scenario.value()); // escenario
        p.put("exp", exp.getEpochSecond()); // caducidad epoch
        p.put("v", version.value()); // versión
        p.put("r", nonce.value()); // nonce/aleatorio
        return p;
    }

    public TokenType type() {
        return type;
    }

    public Label label() {
        return label;
    }

    public Scenario scenario() {
        return scenario;
    }

    public TtlSeconds ttl() {
        return ttl;
    }

    public Version version() {
        return version;
    }

    public Nonce nonce() {
        return nonce;
    }

    public Instant exp() {
        return exp;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Token t))
            return false;
        return type == t.type && Objects.equals(label, t.label) && Objects.equals(scenario, t.scenario)
                && Objects.equals(ttl, t.ttl) && Objects.equals(version, t.version)
                && Objects.equals(nonce, t.nonce) && Objects.equals(exp, t.exp);
    }

    @Override
    public int hashCode() {
        return Objects.hash(type, label, scenario, ttl, version, nonce, exp);
    }
}
