package com.canaryforge.domain.token;

public record TokenPayload(
        String t, // tipo: URL|PIX|KEY
        String label,
        String sc, // escenario
        long exp,
        int v,
        String r // random/uuid
) {
}
