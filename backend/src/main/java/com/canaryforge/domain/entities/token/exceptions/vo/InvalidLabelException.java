package com.canaryforge.domain.entities.token.exceptions.vo;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidLabelException extends DomainException {
    public InvalidLabelException(String msg) {
        super("INVALID_LABEL", msg);
    }
}
