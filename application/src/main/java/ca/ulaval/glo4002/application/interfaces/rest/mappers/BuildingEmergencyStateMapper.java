package ca.ulaval.glo4002.application.interfaces.rest.mappers;

import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import ca.ulaval.glo4002.application.interfaces.rest.responses.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class BuildingEmergencyStateMapper{
  public static BuildingEmergencyStateResponse toResponse(Building building){
    String buildingId = building.getId().toString();
    List<Zone> zones = building.getZones();
    List<Door> doors = new ArrayList<>();
    for (Zone zone : zones){
      doors.addAll(zone.getDoors());
    }

    int totalNumberOfAgentsInZone = 0;
    for (Zone zone : zones){
      totalNumberOfAgentsInZone += zone.getNumberOfAgentsRequestedInZoneAndAllRooms();
    }
    AgentResponse agents = new AgentResponse(totalNumberOfAgentsInZone);

    List<ZoneResponse> zoneResponses = zones.stream()
        .map(BuildingEmergencyStateMapper::mapZoneToResponse).collect(Collectors.toList());

    List<DoorResponse> doorResponses = doors.stream()
        .map(door -> new DoorResponse(door.getId().toString(),!door.getIsOpen(),door.getIsLocked()))
        .collect(Collectors.toList());

    return new BuildingEmergencyStateResponse(buildingId,agents,zoneResponses,doorResponses);
  }

  private static ZoneResponse mapZoneToResponse(Zone zone){
    String zoneId = zone.getId().toString();
    String fireState = zone.getUrgencyState().toString();
    boolean hasSmoke = zone.getSmokeConcentration() > 0;

    VentilationSystem ventilationSystem = zone.getVentilationSystem();
    Map<String, String> ventilationSettings = ventilationSystem.getCurrentState();

    int currentOccupation = zone.getTotalOccupation();
    List<String> identifiedOccupants = zone.getIdentifiedOccupants();
    Map<String, Integer> consecutiveAccesses = zone.getConsecutiveAccessesByPerson();

    if (ventilationSettings.containsKey("distributionSpeed")
        && ventilationSettings.containsKey("returnSpeed")){
      double distributionSpeed = Double.parseDouble(ventilationSettings.get("distributionSpeed"));
      double returnSpeed = Double.parseDouble(ventilationSettings.get("returnSpeed"));
      return new AdjustablePressureVentilationZoneResponse(zoneId,fireState,hasSmoke,
          distributionSpeed,returnSpeed,currentOccupation,identifiedOccupants,consecutiveAccesses);
    } else if (ventilationSettings.containsKey("isOpen")){
      String ventilationState = ventilationSettings.get("isOpen");
      return new SimpleVentilationZoneResponse(zoneId,fireState,hasSmoke,ventilationState,
          currentOccupation,identifiedOccupants,consecutiveAccesses);
    }

    throw new IllegalStateException("Unknown ventilation system type for zone: " + zoneId);
  }
}
