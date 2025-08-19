package com.canaryforge.domain.entities.token.exceptions.vo;

import com.canaryforge.domain.entities.token.exceptions.DomainException;

public class InvalidTtlException extends DomainException {
    public InvalidTtlException(String msg) {
        super("INVALID_TTL", msg);
    }
}
