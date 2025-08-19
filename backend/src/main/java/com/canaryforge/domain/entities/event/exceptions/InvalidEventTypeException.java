package com.canaryforge.domain.entities.event.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidEventTypeException extends DomainException {
    public InvalidEventTypeException(String msg) {
        super("INVALID_EVENT_TYPE", msg);
    }
}
