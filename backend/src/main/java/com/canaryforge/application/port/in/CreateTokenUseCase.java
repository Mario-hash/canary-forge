package com.canaryforge.application.port.in;

import com.canaryforge.application.command.CreateTokenCommand;

public interface CreateTokenUseCase {
    String create(CreateTokenCommand cmd);
}
