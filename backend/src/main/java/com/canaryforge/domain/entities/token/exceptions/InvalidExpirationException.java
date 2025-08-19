package com.canaryforge.domain.entities.token.exceptions;

public class InvalidExpirationException extends DomainException {
    public InvalidExpirationException(String msg) {
        super("INVALID_EXP", msg);
    }
}
