package com.canaryforge.domain.entities.event.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidCorrelationIdException extends DomainException {
    public InvalidCorrelationIdException(String msg) {
        super("INVALID_CORRELATION_ID", msg);
    }
}
