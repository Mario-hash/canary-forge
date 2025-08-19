package com.canaryforge.adapter.web;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.canaryforge.adapter.web.dto.CreateTokenRequest;
import com.canaryforge.adapter.web.dto.TokenResponse;
import com.canaryforge.application.command.CreateTokenCommand;
import com.canaryforge.application.port.in.CreateTokenUseCase;
import com.canaryforge.domain.entities.token.exceptions.UnsupportedTokenTypeException;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api")
public class TokenController {
    private final CreateTokenUseCase createTokenUseCase;

    public TokenController(CreateTokenUseCase createTokenUseCase) {
        this.createTokenUseCase = createTokenUseCase;
    }

    @PostMapping("/tokens")
    public TokenResponse create(@RequestBody CreateTokenRequest r) {
        CreateTokenCommand cmd = new CreateTokenCommand(r.type(), r.label(), r.scenario(), r.ttlSec());
        String sig = createTokenUseCase.create(cmd);

        return switch (r.type()) {
            case "PIX" -> {
                String url = "/p/" + sig;
                String html = "<img src=\"" + url + "\" width=\"1\" height=\"1\" style=\"display:none\"/>";
                yield new TokenResponse(url, html, null, null);
            }
            default -> new TokenResponse("/c/" + sig, null, null, null);
        };
    }

}