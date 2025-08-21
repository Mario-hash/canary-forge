package com.canaryforge.domain.entities.event;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.canaryforge.domain.entities.common.Version;
import com.canaryforge.domain.entities.event.vo.Attributes;
import com.canaryforge.domain.entities.event.vo.CausationId;
import com.canaryforge.domain.entities.event.vo.CorrelationId;
import com.canaryforge.domain.entities.event.vo.EventId;
import com.canaryforge.domain.entities.event.vo.EventType;
import com.canaryforge.domain.entities.event.vo.JsonPayload;
import com.canaryforge.domain.entities.event.vo.OccurredAt;
import com.canaryforge.domain.entities.event.vo.Producer;

class EventTest {

    // Helpers de datos válidos
    private static final EventType TYPE = EventType.CLICK;
    private static final OccurredAt WHEN = new OccurredAt(java.time.Instant.EPOCH); // pasado => siempre válido
    private static final Producer PROD = new Producer("svc-checkout");
    private static final Version V1 = Version.of(1);
    private static final CorrelationId CID = new CorrelationId(UUID.randomUUID());
    private static final CausationId CAU = new CausationId(UUID.randomUUID());
    private static final JsonPayload PAYLOAD = JsonPayload.of("{\"ok\":true}");
    private static final Attributes ATTRS = Attributes.of(Map.of("k", "v"));

    @Test
    @DisplayName("create(): genera Event con id no nulo y mantiene todos los campos y opcionales")
    void create_happyPath() {
        Event e = Event.create(TYPE, WHEN, PROD, V1, CID, CAU, PAYLOAD, ATTRS);

        assertNotNull(e.id());
        assertEquals(TYPE, e.type());
        assertEquals(WHEN, e.occurredAt());
        assertEquals(PROD, e.producer());
        assertEquals(V1, e.version());
        assertEquals(CID, e.correlationId());
        assertEquals(CAU, e.causationId());
        assertEquals(PAYLOAD, e.payload());
        assertEquals(ATTRS, e.attributes());
        assertEquals("v", e.attributes().value().get("k"));
    }

    @Test
    @DisplayName("create(): genera ids distintos en invocaciones consecutivas")
    void create_generatesDifferentIds() {
        Event e1 = Event.create(TYPE, WHEN, PROD, V1, CID, CAU, PAYLOAD, ATTRS);
        Event e2 = Event.create(TYPE, WHEN, PROD, V1, CID, CAU, PAYLOAD, ATTRS);
        assertNotEquals(e1.id(), e2.id());
    }

    @Test
    @DisplayName("create(): attributes == null ⇒ attributes vacíos")
    void create_nullAttributes_becomesEmpty() {
        Event e = Event.create(TYPE, WHEN, PROD, V1, CID, CAU, PAYLOAD, null);
        assertTrue(e.attributes().value().isEmpty());
    }

    @Test
    @DisplayName("create(): opcionales null (correlationId/causationId/payload) son aceptados")
    void create_optionalNulls_allowed() {
        Event e = Event.create(TYPE, WHEN, PROD, V1, null, null, null, ATTRS);
        assertNull(e.correlationId());
        assertNull(e.causationId());
        assertNull(e.payload());
        assertEquals(ATTRS, e.attributes());
    }

    @Test
    @DisplayName("restore(): respeta el id pasado y todos los campos")
    void restore_happyPath() {
        EventId id = new EventId(UUID.randomUUID());
        Event e = Event.restore(id, TYPE, WHEN, PROD, V1, CID, CAU, PAYLOAD, ATTRS);

        assertEquals(id, e.id());
        assertEquals(TYPE, e.type());
        assertEquals(WHEN, e.occurredAt());
        assertEquals(PROD, e.producer());
        assertEquals(V1, e.version());
        assertEquals(CID, e.correlationId());
        assertEquals(CAU, e.causationId());
        assertEquals(PAYLOAD, e.payload());
        assertEquals(ATTRS, e.attributes());
    }

    @Test
    @DisplayName("restore(): attributes == null ⇒ attributes vacíos")
    void restore_nullAttributes_becomesEmpty() {
        EventId id = new EventId(UUID.randomUUID());
        Event e = Event.restore(id, TYPE, WHEN, PROD, V1, CID, CAU, PAYLOAD, null);
        assertTrue(e.attributes().value().isEmpty());
    }

    // ------ Validaciones de null obligatorios (mensajes de requireNonNull) ------

    @Test
    @DisplayName("restore(): id null ⇒ NullPointerException con mensaje 'id'")
    void restore_nullId_throwsNpeWithIdMessage() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> Event.restore(null, TYPE, WHEN, PROD, V1, CID, CAU, PAYLOAD, ATTRS));
        assertEquals("id", ex.getMessage());
    }

    @Test
    @DisplayName("create(): type null ⇒ NullPointerException con mensaje 'type'")
    void create_nullType_throwsNpeWithTypeMessage() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> Event.create(null, WHEN, PROD, V1, CID, CAU, PAYLOAD, ATTRS));
        assertEquals("type", ex.getMessage());
    }

    @Test
    @DisplayName("create(): occurredAt null ⇒ NullPointerException con mensaje 'occurredAt'")
    void create_nullOccurredAt_throwsNpeWithOccurredAtMessage() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> Event.create(TYPE, null, PROD, V1, CID, CAU, PAYLOAD, ATTRS));
        assertEquals("occurredAt", ex.getMessage());
    }

    @Test
    @DisplayName("create(): producer null ⇒ NullPointerException con mensaje 'producer'")
    void create_nullProducer_throwsNpeWithProducerMessage() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> Event.create(TYPE, WHEN, null, V1, CID, CAU, PAYLOAD, ATTRS));
        assertEquals("producer", ex.getMessage());
    }

    @Test
    @DisplayName("create(): version null ⇒ NullPointerException con mensaje 'version'")
    void create_nullVersion_throwsNpeWithVersionMessage() {
        NullPointerException ex = assertThrows(
                NullPointerException.class,
                () -> Event.create(TYPE, WHEN, PROD, null, CID, CAU, PAYLOAD, ATTRS));
        assertEquals("version", ex.getMessage());
    }
}
