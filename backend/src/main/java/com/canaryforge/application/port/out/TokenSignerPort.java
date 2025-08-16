package com.canaryforge.application.port.out;

import java.util.Map;

import com.canaryforge.domain.token.TokenPayload;

public interface TokenSignerPort {
    String sign(Map<String, Object> payload);

    TokenPayload verify(String sig);
}
