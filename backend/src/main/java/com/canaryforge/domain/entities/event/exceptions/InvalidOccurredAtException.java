package com.canaryforge.domain.entities.event.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidOccurredAtException extends DomainException {
    public InvalidOccurredAtException(String msg) {
        super("INVALID_OCCURRED_AT", msg);
    }
}
