package com.canaryforge.domain.entities.event.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidAttributesException extends DomainException {
    public InvalidAttributesException(String msg) {
        super("INVALID_ATTRIBUTES", msg);
    }
}