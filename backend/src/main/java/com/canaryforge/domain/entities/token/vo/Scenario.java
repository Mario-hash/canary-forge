package com.canaryforge.domain.entities.token.vo;

import java.util.Objects;

import com.canaryforge.domain.entities.token.exceptions.vo.InvalidScenarioException;

public final class Scenario {
    private static final int MAX = 60;
    private static final String RE = "^[A-Za-z0-9_-]{1,60}$";
    private final String value;

    private Scenario(String value) {
        this.value = value;
    }

    public static Scenario of(String raw) {
        if (raw == null || raw.isBlank())
            throw new InvalidScenarioException("scenario is blank");
        if (raw.length() > MAX)
            throw new InvalidScenarioException("scenario too long");
        if (!raw.matches(RE))
            throw new InvalidScenarioException("scenario must match " + RE);
        return new Scenario(raw);
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
        return o instanceof Scenario s && Objects.equals(value, s.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }
}
