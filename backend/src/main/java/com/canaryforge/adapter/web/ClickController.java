package com.canaryforge.adapter.web;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.canaryforge.application.port.in.RegisterHitUseCase;

import reactor.core.publisher.Mono;

@RestController
public class ClickController {
    private final RegisterHitUseCase registerHit;

    public ClickController(RegisterHitUseCase registerHit) {
        this.registerHit = registerHit;
    }

    @GetMapping("/c/{sig}")
    public Mono<ResponseEntity<Void>> click(@PathVariable String sig,
            @RequestHeader(value = "User-Agent", required = false) String ua,
            @RequestHeader(value = "Referer", required = false) String ref,
            ServerHttpRequest req) {
        String ipTrunc = NetUtil.ipTruncFrom(req.getRemoteAddress());
        return registerHit.register(sig, ua, ref, ipTrunc)
                .thenReturn(ResponseEntity.noContent().build());
    }
}
