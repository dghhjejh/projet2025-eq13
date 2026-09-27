package ca.ulaval.glo4002.application.infrastructure.factories;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.campus.zones.*;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import java.util.List;

public class ZoneFactory{

  public Zone createZone(ZoneId zoneId,ZoneType type,boolean currentlyHasActivity,List<Door> doors,
      List<Room> rooms,UrgencyState urgencyState,int smokeConcentration,boolean hasActiveAlarms,
      VentilationSystem ventilationSystem,List<RequestAgent> agentsRequested,
      List<Agent> agentsDeployedInZone,List<Agent> agentsArrivedInZone,
      List<Gathering> gatheringsInZone){
    if (type == ZoneType.HAZARDOUS_MATERIAL){
      return new HazardousMaterialZone(zoneId,type,currentlyHasActivity,doors,rooms,urgencyState,
          smokeConcentration,hasActiveAlarms,ventilationSystem,agentsRequested,agentsDeployedInZone,
          agentsArrivedInZone,gatheringsInZone);
    }
    return new NormalZone(zoneId,type,currentlyHasActivity,doors,rooms,urgencyState,
        smokeConcentration,hasActiveAlarms,ventilationSystem,agentsRequested,agentsDeployedInZone,
        agentsArrivedInZone,gatheringsInZone);
  }
}
