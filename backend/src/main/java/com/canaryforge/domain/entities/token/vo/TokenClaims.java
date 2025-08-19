// backend/src/main/java/com/canaryforge/domain/entities/token/vo/TokenClaims.java
package com.canaryforge.domain.entities.token.vo;

import java.time.Instant;
import java.util.Objects;

import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.token.exceptions.InvalidExpirationException;

public final class TokenClaims {
    private final TokenType tokenType;
    private final Label label;
    private final Scenario scenario;
    private final Version version;
    private final Nonce requestId;
    private final Instant expiration;

    private TokenClaims(TokenType tokenType,
            Label label,
            Scenario scenario,
            Version version,
            Nonce requestId,
            Instant expiration) {
        this.tokenType = Objects.requireNonNull(tokenType, "tokenType");
        this.label = Objects.requireNonNull(label, "label");
        this.scenario = Objects.requireNonNull(scenario, "scenario");
        this.version = Objects.requireNonNull(version, "version");
        this.requestId = Objects.requireNonNull(requestId, "requestId");
        this.expiration = Objects.requireNonNull(expiration, "expiration");
        if (expiration.isBefore(Instant.now())) {
            throw new InvalidExpirationException("token expired");
        }
    }

    public static TokenClaims of(TokenType tokenType,
            Label label,
            Scenario scenario,
            Version version,
            Nonce requestId,
            Instant expiration) {
        return new TokenClaims(tokenType, label, scenario, version, requestId, expiration);
    }

    public static TokenClaims ofEpochSeconds(TokenType tokenType,
            Label label,
            Scenario scenario,
            Version version,
            Nonce requestId,
            long expirationEpochSeconds) {
        return of(tokenType, label, scenario, version, requestId, Instant.ofEpochSecond(expirationEpochSeconds));
    }

    public TokenType tokenType() {
        return tokenType;
    }

    public Label label() {
        return label;
    }

    public Scenario scenario() {
        return scenario;
    }

    public Version version() {
        return version;
    }

    public Nonce requestId() {
        return requestId;
    }

    public Instant expiration() {
        return expiration;
    }

    @Override
    public String toString() {
        return "TokenClaims[" + tokenType + "," + label + "," + scenario + ",v=" + version.value() + ",r="
                + requestId.value() + ",exp=" + expiration + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof TokenClaims that))
            return false;
        return tokenType == that.tokenType &&
                label.equals(that.label) &&
                scenario.equals(that.scenario) &&
                version.value() == that.version.value() &&
                requestId.equals(that.requestId) &&
                expiration.equals(that.expiration);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tokenType, label, scenario, version.value(), requestId, expiration);
    }
}
