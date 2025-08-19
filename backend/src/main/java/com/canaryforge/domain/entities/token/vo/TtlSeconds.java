package com.canaryforge.domain.entities.token.vo;

import com.canaryforge.domain.entities.token.exceptions.vo.InvalidTtlException;

public final class TtlSeconds {
    public static final int MIN = 60;
    public static final int MAX = 60 * 60 * 24 * 30; // 30 días
    private final int value;

    private TtlSeconds(int value) {
        this.value = value;
    }

    public static TtlSeconds of(int raw) {
        if (raw < MIN || raw > MAX)
            throw new InvalidTtlException("ttlSec out of range (60..2592000)");
        return new TtlSeconds(raw);
    }

    public int value() {
        return value;
    }
}
