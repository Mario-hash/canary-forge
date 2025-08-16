package com.canaryforge.adapter.persistence;

import java.time.Instant;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("events")
public class EventDoc {
    @Id
    public String id;
    public String type;
    public String tokenType;
    public String label;
    public String scenario;
    public String ipTrunc;
    public String ua;
    public String referrer;
    public String severity;
    public Instant createdAt;
}
