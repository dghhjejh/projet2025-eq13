package ca.ulaval.glo4002.application.infrastructure.repositories.mappers;

import ca.ulaval.glo4002.application.domain.campus.zones.UrgencyState;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.ZoneStateDTO;

public class ZoneStateMapper{

  public ZoneStateDTO toZoneStateDTO(Zone zone){
    return new ZoneStateDTO(zone.getUrgencyState().toString(),zone.getSmokeConcentration(),
        zone.areAlarmsActive());
  }

  public UrgencyState toUrgencyState(ZoneStateDTO dto){
    if (dto == null || dto.urgencyState() == null){
      return UrgencyState.NORMAL;
    }
    return UrgencyState.fromString(dto.urgencyState());
  }

  public int toSmokeConcentration(ZoneStateDTO dto){
    return dto != null ? dto.smokeConcentration() : 0;
  }

  public boolean toAlarmsActive(ZoneStateDTO dto){
    return dto != null && dto.areAlarmsActive();
  }
}
