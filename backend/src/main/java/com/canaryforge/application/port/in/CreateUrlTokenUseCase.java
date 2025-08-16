package com.canaryforge.application.port.in;

public interface CreateUrlTokenUseCase {
    String create(String label, String scenario, int ttlSec);
}
