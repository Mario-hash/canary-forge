package com.canaryforge.adapter.persistence;

import java.time.Instant;
import java.util.Map;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("events")
public class EventDoc {
    @Id
    public String id; // UUID string (mismo que EventId)

    @Indexed
    public String type; // EventType.name()

    public String producer; // Producer.value()
    public Integer version; // Version.value()
    @Indexed
    public Instant occurredAt; // OccurredAt.value()

    public String correlationId; // UUID string (nullable)
    public String causationId; // UUID string (nullable)

    public String payloadJson; // JSON en texto (nullable)
    public Map<String, String> attributes; // metadata ligera (nullable)

    // --- (Opcional) compat: si quieres conservar los campos legacy:
    // public String tokenType;
    // public String label;
    // public String scenario;
    // public String ipTrunc;
    // public String ua;
    // public String referrer;
    // public String severity; // si ya no lo usas, puedes omitirlo
    // public Instant createdAt; // alias legacy de occurredAt
}
