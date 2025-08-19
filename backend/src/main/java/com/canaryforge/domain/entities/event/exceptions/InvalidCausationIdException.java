package com.canaryforge.domain.entities.event.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidCausationIdException extends DomainException {
    public InvalidCausationIdException(String msg) {
        super("INVALID_CAUSATION_ID", msg);
    }
}
