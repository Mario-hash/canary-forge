package com.canaryforge.application.command;

public record CreateTokenCommand(
        String type,
        String label,
        String scenario,
        int ttlSec) {
}
