package com.canaryforge.application.usecase;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.Map;

import com.canaryforge.application.command.RegisterHitCommand;
import com.canaryforge.application.port.in.RegisterHitUseCase;
import com.canaryforge.application.port.out.ClockPort;
import com.canaryforge.application.port.out.EventPublisherPort;
import com.canaryforge.application.port.out.EventStorePort;
import com.canaryforge.application.port.out.TokenSignerPort;
import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.event.Event;
import com.canaryforge.domain.entities.event.vo.Attributes;
import com.canaryforge.domain.entities.event.vo.CausationId;
import com.canaryforge.domain.entities.event.vo.CorrelationId;
import com.canaryforge.domain.entities.event.vo.EventType;
import com.canaryforge.domain.entities.event.vo.JsonPayload;
import com.canaryforge.domain.entities.event.vo.OccurredAt;
import com.canaryforge.domain.entities.event.vo.Producer;
import com.canaryforge.domain.entities.token.vo.TokenClaims;

import reactor.core.publisher.Mono;

public class RegisterHitUseCaseImpl implements RegisterHitUseCase {

    private final TokenSignerPort signer;
    private final EventStorePort store;
    private final EventPublisherPort pub;
    private final ClockPort clock;

    public RegisterHitUseCaseImpl(TokenSignerPort signer, EventStorePort store, EventPublisherPort pub,
            ClockPort clock) {
        this.signer = signer;
        this.store = store;
        this.pub = pub;
        this.clock = clock;
    }

    @Override
    public Mono<Event> register(RegisterHitCommand c) {
        // 1) Verificar token (regla de negocio previa al evento)
        TokenClaims claims = signer.verify(c.sig()); // lanza si inválido/expirado

        // 2) Derivar info de negocio (severity/productor/atributos)
        SeverityLevel sev = deriveSeverity(c.userAgent(), c.referrer()); // dominio nuevo (si lo representas)
        Producer producer = new Producer(c.producer());
        EventType type = EventType.from(c.eventType());
        OccurredAt occurred = occurredAtFrom(c.occurredAtIso());

        // Atributos ligeros del contexto (clave-valor)
        Map<String, String> attrsMap = Map.of(
                "tokenType", claims.tokenType().name(),
                "label", claims.label().value(),
                "scenario", claims.scenario().value(),
                "ipTrunc", c.ipTrunc() == null ? "" : c.ipTrunc(),
                "nonce", claims.requestId().value());
        Attributes attributes = Attributes.of(attrsMap);

        // Payload JSON opcional (si quieres serializar más contexto)
        JsonPayload payload = JsonPayload.of(null); // o algún JSON con más datos

        // Versión del evento (contrato)
        Version version = Version.v1();

        // Correlación/causación opcionales (no las tenemos aquí)
        CorrelationId corr = null;
        CausationId caus = null;

        // 3) Crear la ENTIDAD DE DOMINIO (nuevo aggregate)
        Event ev = Event.create(
                type, occurred, producer, version, corr, caus, payload, attributes);

        // 4) Persistir y publicar (reactivo)
        return store.save(ev)
                .doOnNext(pub::publish);
    }

    private OccurredAt occurredAtFrom(String iso) {
        if (iso == null || iso.isBlank()) {
            return new OccurredAt(clock.now());
        }
        try {
            return new OccurredAt(Instant.parse(iso));
        } catch (DateTimeParseException e) {
            // si prefieres, lanza InvalidOccurredAtException
            return new OccurredAt(clock.now());
        }
    }

    // Puedes modelar severidad con un VO (SeverityLevel) o como atributo
    private SeverityLevel deriveSeverity(String ua, String ref) {
        String s = (ua == null ? "" : ua).toLowerCase();
        boolean previewer = s.contains("slack") || s.contains("twitterbot") || s.contains("linkedin")
                || s.contains("discord");
        return previewer ? SeverityLevel.LOW : SeverityLevel.MEDIUM;
    }

    // ejemplo simple de VO de severidad (si lo quieres en el dominio nuevo)
    public enum SeverityLevel {
        LOW, MEDIUM, HIGH
    }
}
