package com.canaryforge.domain.entities.event.vo;

import com.canaryforge.domain.entities.event.exceptions.InvalidAttributesException;
import java.util.Collections;
import java.util.Map;

public final class Attributes {
    private static final int MAX_ENTRIES = 20;
    private static final int MAX_KEY = 50;
    private static final int MAX_VAL = 200;
    private final Map<String, String> value;

    private Attributes(Map<String, String> value) {
        this.value = value;
    }

    public static Attributes of(Map<String, String> map) {
        if (map == null || map.isEmpty())
            return empty();
        if (map.size() > MAX_ENTRIES)
            throw new InvalidAttributesException("too many attributes (max " + MAX_ENTRIES + ")");
        for (var e : map.entrySet()) {
            var k = e.getKey();
            var v = e.getValue();
            if (k == null || k.isBlank())
                throw new InvalidAttributesException("attribute key cannot be empty");
            if (k.length() > MAX_KEY)
                throw new InvalidAttributesException("attribute key too long (max " + MAX_KEY + ")");
            if (v != null && v.length() > MAX_VAL)
                throw new InvalidAttributesException("attribute value too long (max " + MAX_VAL + ")");
        }
        return new Attributes(Collections.unmodifiableMap(map));
    }

    public static Attributes empty() {
        return new Attributes(Collections.emptyMap());
    }

    public Map<String, String> value() {
        return value;
    }
}
