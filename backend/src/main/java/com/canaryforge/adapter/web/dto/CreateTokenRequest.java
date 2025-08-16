package com.canaryforge.adapter.web.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record CreateTokenRequest(
        @Pattern(regexp = "URL|PIX|KEY") String type,
        @NotBlank String label,
        @NotBlank String scenario,
        @Min(60) @Max(60 * 60 * 24 * 30) int ttlSec) {
}
