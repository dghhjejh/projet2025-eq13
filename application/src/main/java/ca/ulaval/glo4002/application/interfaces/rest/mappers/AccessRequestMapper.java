package ca.ulaval.glo4002.application.interfaces.rest.mappers;

import ca.ulaval.glo4002.application.domain.access.AccessRequestEvent;
import ca.ulaval.glo4002.application.domain.access.id.CardId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.interfaces.rest.requests.AccessRequest;
import java.time.LocalTime;

public class AccessRequestMapper{

  public AccessRequestEvent toAccessRequestEvent(AccessRequest request){
    if (request == null){
      throw new IllegalArgumentException("Invalid request : AccessRequest cannot be null");
    }

    CardId cardId = (request.card_id.isEmpty() || request.card_id.equals("null"))
        ? null
        : new CardId(request.card_id);
    ZoneId zoneId = new ZoneId(request.zone_id);
    RoomId roomId = (request.room_id.isEmpty() || request.room_id.equals("null"))
        ? null
        : new RoomId(request.room_id);
    DoorId doorId = (request.door_id.isEmpty() || request.door_id.equals("null"))
        ? null
        : new DoorId(request.door_id);
    LocalTime time = request.date_heure;

    return new AccessRequestEvent(cardId,zoneId,roomId,doorId,time);
  }
}
