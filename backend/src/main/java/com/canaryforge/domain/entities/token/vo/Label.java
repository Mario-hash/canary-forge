package com.canaryforge.domain.entities.token.vo;

import java.util.Objects;

import com.canaryforge.domain.entities.token.exceptions.vo.InvalidLabelException;

public final class Label {
    private static final int MAX = 60;
    private static final String RE = "^[A-Za-z0-9_-]{1,60}$";
    private final String value;

    private Label(String value) {
        this.value = value;
    }

    public static Label of(String raw) {
        if (raw == null || raw.isBlank())
            throw new InvalidLabelException("label is blank");
        if (raw.length() > MAX)
            throw new InvalidLabelException("label too long");
        if (!raw.matches(RE))
            throw new InvalidLabelException("label must match " + RE);
        return new Label(raw);
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
        return o instanceof Label l && Objects.equals(value, l.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
