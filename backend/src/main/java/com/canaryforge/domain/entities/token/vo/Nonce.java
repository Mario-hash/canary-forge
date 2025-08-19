package com.canaryforge.domain.entities.token.vo;

import java.util.Objects;
import java.util.UUID;

public final class Nonce {
    private final String value;

    private Nonce(String v) {
        this.value = v;
    }

    public static Nonce random() {
        return new Nonce(UUID.randomUUID().toString());
    }

    public static Nonce of(String v) {
        if (v == null || v.isBlank())
            throw new IllegalArgumentException("nonce is blank");
        return new Nonce(v);
    }

    public String value() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Nonce n && Objects.equals(n.value, value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
