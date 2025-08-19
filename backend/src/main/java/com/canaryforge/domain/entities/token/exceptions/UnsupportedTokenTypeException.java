package com.canaryforge.domain.entities.token.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class UnsupportedTokenTypeException extends DomainException {
    public UnsupportedTokenTypeException(String msg) {
        super("INVALID_TYPE", msg);
    }
}
