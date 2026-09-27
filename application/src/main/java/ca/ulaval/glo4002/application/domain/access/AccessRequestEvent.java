package ca.ulaval.glo4002.application.domain.access;

import ca.ulaval.glo4002.application.domain.access.id.CardId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.time.LocalTime;

public record AccessRequestEvent(CardId cardId, ZoneId zoneId, RoomId roomId, DoorId doorId,
    LocalTime time) {
}
