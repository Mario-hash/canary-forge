package com.canaryforge.config;

import com.canaryforge.adapter.crypto.HmacTokenSignerAdapter;
import com.canaryforge.adapter.persistence.EventRepository;
import com.canaryforge.adapter.persistence.MongoEventStoreAdapter;
import com.canaryforge.adapter.realtime.SseEventPublisherAdapter;
import com.canaryforge.adapter.system.RandomUuidAdapter;
import com.canaryforge.adapter.system.SystemClockAdapter;
import com.canaryforge.application.port.in.CreateUrlTokenUseCase;
import com.canaryforge.application.port.in.RegisterHitUseCase;
import com.canaryforge.application.port.out.*;
import com.canaryforge.application.service.CreateUrlTokenService;
import com.canaryforge.application.service.RegisterHitService;
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
    IdGeneratorPort idGeneratorPort() {
        return new RandomUuidAdapter();
    }

    @Bean
    CreateUrlTokenUseCase createUrlTokenUseCase(TokenSignerPort s, ClockPort c, IdGeneratorPort ids) {
        return new CreateUrlTokenService(s, c, ids);
    }

    @Bean
    RegisterHitUseCase registerHitUseCase(TokenSignerPort s, EventStorePort st, EventPublisherPort p, ClockPort c) {
        return new RegisterHitService(s, st, p, c);
    }
}
