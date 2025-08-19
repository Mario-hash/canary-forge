package com.canaryforge.domain.entities.token.exceptions.vo;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidTtlException extends DomainException {
    public InvalidTtlException(String msg) {
        super("INVALID_TTL", msg);
    }
}
