package com.canaryforge.domain.entities.token.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidExpirationException extends DomainException {
    public InvalidExpirationException(String msg) {
        super("INVALID_EXP", msg);
    }
}
