package com.canaryforge.application.port.out;

import java.util.Map;

import com.canaryforge.domain.entities.token.vo.TokenClaims;

public interface TokenSignerPort {
    String sign(Map<String, Object> payload);

    TokenClaims verify(String sig);
}
