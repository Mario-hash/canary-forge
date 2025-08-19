package com.canaryforge.domain.entities.token.exceptions.vo;

import com.canaryforge.domain.entities.common.DomainException;

public class InvalidScenarioException extends DomainException {
    public InvalidScenarioException(String msg) {
        super("INVALID_SCENARIO", msg);
    }
}