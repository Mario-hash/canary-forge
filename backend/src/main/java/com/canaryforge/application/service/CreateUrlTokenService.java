package com.canaryforge.application.service;

import java.util.HashMap;
import java.util.Map;

import com.canaryforge.application.port.in.CreateUrlTokenUseCase;
import com.canaryforge.application.port.out.ClockPort;
import com.canaryforge.application.port.out.IdGeneratorPort;
import com.canaryforge.application.port.out.TokenSignerPort;

public class CreateUrlTokenService implements CreateUrlTokenUseCase {

    private final TokenSignerPort signer;
    private final ClockPort clock;
    private final IdGeneratorPort ids;

    public CreateUrlTokenService(TokenSignerPort signer, ClockPort clock, IdGeneratorPort ids) {
        this.signer = signer;
        this.clock = clock;
        this.ids = ids;
    }

    @Override
    public String create(String label, String scenario, int ttlSec) {
        Map<String, Object> p = new HashMap<>();
        p.put("t", "URL");
        p.put("label", label);
        p.put("sc", scenario);
        p.put("exp", clock.now().getEpochSecond() + ttlSec);
        p.put("v", 1);
        p.put("r", ids.uuid());
        return signer.sign(p);
    }
}
