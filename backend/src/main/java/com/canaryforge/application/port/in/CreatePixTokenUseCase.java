package com.canaryforge.application.port.in;

public interface CreatePixTokenUseCase {
    String create(String label, String scenario, int ttlSec);
}
