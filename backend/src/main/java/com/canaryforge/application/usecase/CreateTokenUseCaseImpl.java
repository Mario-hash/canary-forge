package com.canaryforge.application.usecase;

import com.canaryforge.application.command.CreateTokenCommand;
import com.canaryforge.application.port.in.CreateTokenUseCase;
import com.canaryforge.application.port.out.ClockPort;
import com.canaryforge.application.port.out.TokenSignerPort;
import com.canaryforge.domain.entities.token.Token;
import com.canaryforge.domain.entities.token.exceptions.UnsupportedTokenTypeException;
import com.canaryforge.domain.entities.token.vo.*;
import java.time.Clock;
import java.time.ZoneOffset;

public class CreateTokenUseCaseImpl implements CreateTokenUseCase {
    private final TokenSignerPort signer;
    private final ClockPort clock;

    public CreateTokenUseCaseImpl(TokenSignerPort signer, ClockPort clock) {
        this.signer = signer;
        this.clock = clock;
    }

    @Override
    public String create(CreateTokenCommand cmd) {
        final TokenType type;
        try {
            type = TokenType.valueOf(cmd.type());
        } catch (IllegalArgumentException ex) {
            throw new UnsupportedTokenTypeException("Unsupported type: " + cmd.type());
        }

        var lab = Label.of(cmd.label());
        var sc = Scenario.of(cmd.scenario());
        var ttl = TtlSeconds.of(cmd.ttlSec());

        Clock fixed = Clock.fixed(clock.now(), ZoneOffset.UTC);
        Token t = Token.create(type, lab, sc, ttl, fixed);

        return signer.sign(t.toPayload());
    }
}
