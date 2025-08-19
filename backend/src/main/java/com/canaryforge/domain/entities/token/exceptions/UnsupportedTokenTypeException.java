package com.canaryforge.domain.entities.token.exceptions;

public class UnsupportedTokenTypeException extends DomainException {
    public UnsupportedTokenTypeException(String msg) {
        super("INVALID_TYPE", msg);
    }
}
