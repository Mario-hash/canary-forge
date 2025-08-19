package com.canaryforge.domain.entities.token.vo;

import com.canaryforge.domain.entities.token.exceptions.UnsupportedTokenTypeException;

public enum TokenType {
    URL, PIX, KEY;

    public static TokenType from(String value) {
        if (value == null) {
            throw new UnsupportedTokenTypeException("TokenType cannot be null");
        }
        try {
            return TokenType.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new UnsupportedTokenTypeException("Unsupported TokenType: " + value);
        }
    }
}
