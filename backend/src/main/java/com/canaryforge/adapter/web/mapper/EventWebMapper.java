package com.canaryforge.adapter.web.mapper;

import java.util.Map;

import com.canaryforge.adapter.web.dto.EventSseDto;
import com.canaryforge.domain.entities.event.Event;

public final class EventWebMapper {
    private EventWebMapper() {
    }

    public static EventSseDto toSse(Event e) {
        Map<String, String> attrs = e.attributes() == null ? Map.of() : e.attributes().value();
        return new EventSseDto(
                e.id().toString(),
                e.type().name(),
                e.occurredAt().value(),
                e.producer().value(),
                e.version().value(),
                attrs);
    }
}
