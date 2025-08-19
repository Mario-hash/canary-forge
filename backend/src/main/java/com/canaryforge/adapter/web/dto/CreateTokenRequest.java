package com.canaryforge.adapter.web.dto;

public record CreateTokenRequest(
                String type,
                String label,
                String scenario,
                int ttlSec) {
}
