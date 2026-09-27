package ca.ulaval.glo4002.application.domain.event;

import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.event.parameters.EventParameter;
import java.time.LocalTime;
import java.util.List;

public record SecurityEvent(SecurityEventName name, ZoneId zoneId, LocalTime time,
    List<EventParameter> parameters) {
}
