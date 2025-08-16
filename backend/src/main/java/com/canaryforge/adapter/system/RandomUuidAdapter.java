package com.canaryforge.adapter.system;

import java.util.UUID;

import com.canaryforge.application.port.out.IdGeneratorPort;

public class RandomUuidAdapter implements IdGeneratorPort {
    @Override
    public String uuid() {
        return UUID.randomUUID().toString();
    }
}
