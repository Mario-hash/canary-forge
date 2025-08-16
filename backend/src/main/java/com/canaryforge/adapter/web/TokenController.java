package com.canaryforge.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canaryforge.adapter.web.dto.CreateTokenRequest;
import com.canaryforge.adapter.web.dto.TokenResponse;
import com.canaryforge.application.port.in.CreateUrlTokenUseCase;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class TokenController {
    private final CreateUrlTokenUseCase createUrl;

    public TokenController(CreateUrlTokenUseCase createUrl) {
        this.createUrl = createUrl;
    }

    @PostMapping("/tokens")
    public Mono<TokenResponse> create(@RequestBody @Valid CreateTokenRequest r) {
        if (!"URL".equals(r.type()))
            throw new IllegalArgumentException("Only URL in I1");
        String sig = createUrl.create(r.label(), r.scenario(), r.ttlSec());
        return Mono.just(new TokenResponse("/c/" + sig, null, null, null));
    }
}