package com.canaryforge.domain.entities.event.exceptions;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidProducerException extends DomainException {
    public InvalidProducerException(String msg) {
        super("INVALID_PRODUCER", msg);
    }
}
