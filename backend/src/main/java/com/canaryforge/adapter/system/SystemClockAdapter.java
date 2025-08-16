package com.canaryforge.adapter.system;

import java.time.Instant;

import com.canaryforge.application.port.out.ClockPort;

public class SystemClockAdapter implements ClockPort {

    @Override
    public Instant now() {
        return Instant.now();
    }

}
