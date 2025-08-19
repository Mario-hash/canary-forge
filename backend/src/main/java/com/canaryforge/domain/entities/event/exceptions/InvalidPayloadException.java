package com.canaryforge.domain.entities.event.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidPayloadException extends DomainException {
    public InvalidPayloadException(String msg) {
        super("INVALID_PAYLOAD", msg);
    }
}
