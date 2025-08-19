package com.canaryforge.domain.entities.token.vo;

import java.util.Objects;

public final class Version {
    private final int value;

    private Version(int v) {
        this.value = v;
    }

    public static Version of(int v) {
        if (v <= 0)
            throw new IllegalArgumentException("version must be > 0");
        return new Version(v);
    }

    public static Version v1() {
        return new Version(1);
    }

    public int value() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Version vv && vv.value == value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
