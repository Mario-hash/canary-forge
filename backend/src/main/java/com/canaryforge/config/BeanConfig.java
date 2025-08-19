package com.canaryforge.config;

import com.canaryforge.adapter.crypto.HmacTokenSignerAdapter;
import com.canaryforge.adapter.persistence.EventRepository;
import com.canaryforge.adapter.persistence.MongoEventStoreAdapter;
import com.canaryforge.adapter.realtime.SseEventPublisherAdapter;
import com.canaryforge.adapter.system.SystemClockAdapter;
import com.canaryforge.application.port.in.CreateTokenUseCase;
import com.canaryforge.application.port.in.RegisterHitUseCase;
import com.canaryforge.application.port.out.*;
import com.canaryforge.application.usecase.CreateTokenUseCaseImpl;
import com.canaryforge.application.usecase.RegisterHitUseCaseImpl;
import com.canaryforge.infrastructure.secrets.SecretsConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    TokenSignerPort tokenSignerPort(SecretsConfig cfg) {
        return new HmacTokenSignerAdapter(cfg);
    }

    @Bean
    EventPublisherPort eventPublisherPort() {
        return new SseEventPublisherAdapter();
    }

    @Bean
    EventStorePort eventStorePort(EventRepository repo) {
        return new MongoEventStoreAdapter(repo);
    }

    @Bean
    ClockPort clockPort() {
        return new SystemClockAdapter();
    }

    @Bean
    public RegisterHitUseCase registerHitUseCase(TokenSignerPort signer,
            EventStorePort store,
            EventPublisherPort pub,
            ClockPort clock) {
        return new RegisterHitUseCaseImpl(signer, store, pub, clock);
    }

    @Bean
    CreateTokenUseCase createTokenUseCase(TokenSignerPort signer, ClockPort clock) {
        return new CreateTokenUseCaseImpl(signer, clock);
    }
}
