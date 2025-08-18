package com.canaryforge.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canaryforge.adapter.web.dto.CreateTokenRequest;
import com.canaryforge.adapter.web.dto.TokenResponse;
import com.canaryforge.application.port.in.CreatePixTokenUseCase;
import com.canaryforge.application.port.in.CreateUrlTokenUseCase;

import jakarta.validation.Valid;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class TokenController {
    private final CreateUrlTokenUseCase createUrl;
    private final CreatePixTokenUseCase createPix;

    public TokenController(CreateUrlTokenUseCase createUrl, CreatePixTokenUseCase createPix) {
        this.createUrl = createUrl;
        this.createPix = createPix;
    }

    @PostMapping("/tokens")
    public Mono<TokenResponse> create(@RequestBody @Valid CreateTokenRequest r) {
        return Mono.fromSupplier(() -> switch (r.type()) {
            case "URL" -> {
                String sig = createUrl.create(r.label(), r.scenario(), r.ttlSec());
                yield new TokenResponse("/c/" + sig, null, null, null);
            }
            case "PIX" -> {
                String sig = createPix.create(r.label(), r.scenario(), r.ttlSec());
                String url = "/p/" + sig;
                String html = "<img src=\"" + url + "\" width=\"1\" height=\"1\" style=\"display:none\"/>";
                yield new TokenResponse(url, html, null, null);
            }
            default -> throw new IllegalArgumentException("Only URL|PIX supported in I1");
        });
    }
}