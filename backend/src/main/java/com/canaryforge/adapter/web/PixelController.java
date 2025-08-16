package com.canaryforge.adapter.web;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.canaryforge.application.port.in.RegisterHitUseCase;

import reactor.core.publisher.Mono;

@RestController
public class PixelController {
    private final RegisterHitUseCase registerHit;

    public PixelController(RegisterHitUseCase registerHit) {
        this.registerHit = registerHit;
    }

    @GetMapping(value = "/p/{sig}", produces = "image/svg+xml")
    public Mono<ResponseEntity<String>> pixel(@PathVariable String sig,
            @RequestHeader(value = "User-Agent", required = false) String ua,
            @RequestHeader(value = "Referer", required = false) String ref,
            org.springframework.http.server.reactive.ServerHttpRequest req) {
        String ipTrunc = NetUtil.ipTruncFrom(req.getRemoteAddress());
        String svg = "<svg xmlns='http://www.w3.org/2000/svg' width='1' height='1'><rect width='1' height='1' fill='transparent'/></svg>";
        return registerHit.register(sig, ua, ref, ipTrunc)
                .thenReturn(ResponseEntity.ok()
                        .header(HttpHeaders.CACHE_CONTROL, "no-store, no-cache, must-revalidate, max-age=0")
                        .header("Pragma", "no-cache")
                        .body(svg));
    }
}
