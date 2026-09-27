package ca.ulaval.glo4002.application.infrastructure.mappers;

import ca.ulaval.glo4002.application.domain.access.AccessEvaluator;
import ca.ulaval.glo4002.application.domain.access.accessRules.DoorAccessDuringFireRule;
import ca.ulaval.glo4002.application.domain.access.accessRules.ExtremeNegativePressureRule;
import ca.ulaval.glo4002.application.domain.access.accessRules.ZoneAccessDuringFireRule;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.campus.Campus;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.buildings.PostalAddress;
import ca.ulaval.glo4002.application.domain.campus.buildings.VentilationSystemType;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.doors.DoorType;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.*;
import ca.ulaval.glo4002.application.domain.campus.zones.UrgencyState;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.domain.campus.zones.ZoneType;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import ca.ulaval.glo4002.application.infrastructure.dto.*;
import ca.ulaval.glo4002.application.infrastructure.factories.DoorFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.RoomFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.VentilationSystemFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.ZoneFactory;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class CampusMapper{

  public Campus toCampus(CampusDto campusDto){
    List<Building> buildings = campusDto.buildings.stream().map(this::toBuilding)
        .collect(Collectors.toList());

    Campus campus = new Campus();
    campus.setId(campusDto.id);
    campus.setName(campusDto.name);
    campus.setBuildings(buildings);
    return campus;
  }

  private Building toBuilding(BuildingDto dto){
    BuildingId id = new BuildingId(dto.id);
    String name = dto.name;
    PostalAddress address = new PostalAddress(dto.postalAddress);
    VentilationSystemType ventilationSystemType = VentilationSystemType
        .valueOf(dto.ventilationSystemType);

    List<Zone> zones = new ArrayList<>();
    for (ZoneDto zoneDto : dto.zones){
      zones.add(toZone(zoneDto,ventilationSystemType));
    }

    return new Building(id,name,address,ventilationSystemType,zones,
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  private Zone toZone(ZoneDto dto,VentilationSystemType ventilationSystemType){
    ZoneId id = new ZoneId(dto.id);
    ZoneType zoneType = ZoneType.valueOf(dto.type);
    boolean currentlyHasActivity = dto.currentlyHasActivity;
    int defaultSmokeConcentration = 0;
    UrgencyState defaultUrgencyState = UrgencyState.NORMAL;
    boolean defaultAlarmState = false;

    List<Room> rooms = dto.rooms != null
        ? dto.rooms.stream().map(this::toRoom).collect(Collectors.toList())
        : new ArrayList<>();

    List<Door> doors = dto.doors != null
        ? dto.doors.stream().map(this::toDoor).collect(Collectors.toList())
        : new ArrayList<>();

    List<Agent> agentsDispatchedInZone = new ArrayList<>();
    List<Agent> agentsArrivedInZone = new ArrayList<>();
    List<RequestAgent> agentsRequests = new ArrayList<>();
    List<Gathering> gatheringsInZone = new ArrayList<>();
    VentilationSystemFactory ventilationSystemFactory = new VentilationSystemFactory();
    VentilationSystem ventilationSystem = ventilationSystemFactory.create(ventilationSystemType);
    ZoneFactory zoneFactory = new ZoneFactory();

    return zoneFactory.createZone(id,zoneType,currentlyHasActivity,doors,rooms,defaultUrgencyState,
        defaultSmokeConcentration,defaultAlarmState,ventilationSystem,agentsRequests,
        agentsDispatchedInZone,agentsArrivedInZone,gatheringsInZone);
  }

  private Room toRoom(RoomDto dto){
    RoomId id = new RoomId(dto.id);
    RoomType roomType = dto.type != null ? RoomType.valueOf(dto.type) : RoomType.GENERAL_PUBLIC;
    boolean supportsElectricClosure = dto.supportsElectricClosure;
    Idul labSafetyResponsible = new Idul(dto.labSafetyResponsible);
    List<Agent> agentsDeployedInRoom = new ArrayList<>();
    List<Agent> agentsArrivedInRoom = new ArrayList<>();
    List<RequestAgent> agentsRequests = new ArrayList<>();

    return RoomFactory.createRoom(id,roomType,supportsElectricClosure,labSafetyResponsible,
        agentsRequests,agentsDeployedInRoom,agentsArrivedInRoom);
  }

  private Door toDoor(DoorDto dto){
    DoorId id = new DoorId(dto.id);
    Boolean isOpen = dto.isOpen;
    Boolean isLocked = dto.isLocked;
    DoorType type = DoorType.valueOf(dto.type);
    Boolean onEvacuationPath = dto.onEvacuationPath;

    return DoorFactory.createDoor(id,type,onEvacuationPath,isLocked,isOpen,isLocked,isOpen);
  }
}
