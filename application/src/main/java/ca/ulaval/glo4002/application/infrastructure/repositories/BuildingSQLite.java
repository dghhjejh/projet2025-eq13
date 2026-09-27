package ca.ulaval.glo4002.application.infrastructure.repositories;

import ca.ulaval.glo4002.application.domain.access.AccessEvaluator;
import ca.ulaval.glo4002.application.domain.access.accessRules.DoorAccessDuringFireRule;
import ca.ulaval.glo4002.application.domain.access.accessRules.ExtremeNegativePressureRule;
import ca.ulaval.glo4002.application.domain.access.accessRules.ZoneAccessDuringFireRule;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.domain.campus.Campus;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.buildings.VentilationSystemType;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.OccupationCounter;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.campus.zones.UrgencyState;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import ca.ulaval.glo4002.application.infrastructure.exceptions.BuildingNotFoundException;
import ca.ulaval.glo4002.application.infrastructure.exceptions.ZoneNotFoundException;
import ca.ulaval.glo4002.application.infrastructure.factories.RoomFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.VentilationSystemFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.ZoneFactory;
import ca.ulaval.glo4002.application.infrastructure.repositories.dao.*;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.*;
import ca.ulaval.glo4002.application.infrastructure.repositories.mappers.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

public class BuildingSQLite implements BuildingRepository{
  private final Connection connection;
  private final CampusLoaderFromAPI campusLoader;

  private final ZoneStateDAO zoneStateDAO;
  private final DoorStateDAO doorStateDAO;
  private final AgentDAO agentDAO;
  private final GatheringDAO gatheringDAO;
  private final VentilationSystemDAO ventilationSystemDAO;
  private final BuildingZoneMappingDAO buildingZoneMappingDAO;
  private final RoomOccupationDAO roomOccupationDAO;

  private final ZoneStateMapper zoneStateMapper;
  private final DoorStateMapper doorStateMapper;
  private final AgentMapper agentMapper;
  private final GatheringMapper gatheringMapper;
  private final VentilationSystemMapper ventilationSystemMapper;
  private final BuildingZoneMappingMapper buildingZoneMappingMapper;
  private final RoomOccupationMapper roomOccupationMapper;

  public BuildingSQLite(Connection connection,CampusLoaderFromAPI campusLoader) {
    this.connection = connection;
    this.campusLoader = campusLoader;

    this.zoneStateDAO = new ZoneStateDAO(connection);
    this.doorStateDAO = new DoorStateDAO(connection);
    this.agentDAO = new AgentDAO(connection);
    this.gatheringDAO = new GatheringDAO(connection);
    this.ventilationSystemDAO = new VentilationSystemDAO(connection);
    this.buildingZoneMappingDAO = new BuildingZoneMappingDAO(connection);
    this.roomOccupationDAO = new RoomOccupationDAO(connection);

    this.zoneStateMapper = new ZoneStateMapper();
    this.doorStateMapper = new DoorStateMapper();
    this.agentMapper = new AgentMapper();
    this.gatheringMapper = new GatheringMapper();
    this.ventilationSystemMapper = new VentilationSystemMapper(new VentilationSystemFactory());
    this.buildingZoneMappingMapper = new BuildingZoneMappingMapper();
    this.roomOccupationMapper = new RoomOccupationMapper();
  }

  @Override
  public Building findBuildingById(BuildingId buildingId){
    Building structureBuilding = getBuildingStructureFromAPI(buildingId);

    List<Zone> zonesWithState = new ArrayList<>();
    for (Zone structureZone : structureBuilding.getZones()){
      Zone zoneWithState = mergeZoneWithState(structureZone,
          structureBuilding.getVentilationSystem());
      zonesWithState.add(zoneWithState);
    }

    return new Building(buildingId,structureBuilding.getName(),structureBuilding.getPostalAddress(),
        structureBuilding.getVentilationSystem(),zonesWithState,
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  @Override
  public Building getParentBuilding(ZoneId zoneId){
    try{
      BuildingId buildingId = buildingZoneMappingDAO.findBuildingIdByZoneId(zoneId);

      if (buildingId != null){
        return findBuildingById(buildingId);
      } else{
        rehydrateAllBuildingStructures();

        buildingId = buildingZoneMappingDAO.findBuildingIdByZoneId(zoneId);
        if (buildingId != null){
          return findBuildingById(buildingId);
        } else{
          throw new ZoneNotFoundException("Could not find zone with id: " + zoneId);
        }
      }
    } catch (SQLException e){
      throw new RuntimeException("Could not query database.",e);
    }
  }

  @Override
  public void saveBuildingState(Building changedBuilding){
    try{
      connection.setAutoCommit(false);

      ensureZonesExist(changedBuilding);

      for (Zone zone : changedBuilding.getZones()){
        saveZoneState(zone);
        saveZoneAgents(zone);
        saveZoneGatherings(zone);
        saveZoneVentilationSystem(zone);
        saveZoneDoors(zone);
        saveRoomsInZone(zone);
      }

      connection.commit();
    } catch (SQLException e){
      rollback();
      throw new RuntimeException("Could not save building state: " + changedBuilding.getId(),e);
    } finally{
      resetAutoCommit();
    }
  }

  @Override
  public void clear(){
    try{
      connection.setAutoCommit(false);

      ventilationSystemDAO.deleteAll();
      agentDAO.deleteAll();
      gatheringDAO.deleteAll();
      doorStateDAO.deleteAll();
      zoneStateDAO.deleteAll();
      roomOccupationDAO.deleteAll();
      buildingZoneMappingDAO.deleteAllZones();
      buildingZoneMappingDAO.deleteAllBuildings();

      connection.commit();
    } catch (SQLException e){
      rollback();
      throw new RuntimeException("Could not clear database",e);
    } finally{
      resetAutoCommit();
    }
  }

  private void rehydrateAllBuildingStructures(){
    try{
      Campus campus = campusLoader.loadCampus();
      connection.setAutoCommit(false);

      for (Building building : campus.getBuildings()){
        buildingZoneMappingDAO.insertBuildingIfNotExists(building.getId());

        List<BuildingZoneMappingDTO> mappings = buildingZoneMappingMapper
            .toBuildingZoneMappingDTOs(building.getId(),building.getZones());
        buildingZoneMappingDAO.insertZoneMappingsBatch(mappings);
      }

      connection.commit();
    } catch (SQLException e){
      rollback();
      throw new RuntimeException("Could not rehydrate building structures",e);
    } finally{
      resetAutoCommit();
    }
  }

  private Building getBuildingStructureFromAPI(BuildingId buildingId){
    Campus campus = campusLoader.loadCampus();
    for (Building building : campus.getBuildings()){
      if (building.getId().equals(buildingId)){
        return building;
      }
    }
    throw new BuildingNotFoundException("Could not find building with id: " + buildingId);
  }

  private Zone mergeZoneWithState(Zone structureZone,VentilationSystemType buildingVentilationType){
    ZoneId zoneId = structureZone.getId();

    try{
      ZoneStateDTO zoneStateDTO = zoneStateDAO.findByZoneId(zoneId);
      List<DoorStateDTO> doorStateDTOs = doorStateDAO.findByZoneId(zoneId);
      List<AgentDTO> deployedAgentDTOs = agentDAO.findDeployedByZoneId(zoneId);
      List<AgentDTO> arrivedAgentDTOs = agentDAO.findArrivedByZoneId(zoneId);
      List<AgentDTO> requestedAgentDTOs = agentDAO.findRequestedByZoneId(zoneId);
      List<GatheringDTO> gatheringDTOs = gatheringDAO.findByZoneId(zoneId);
      VentilationSystemDTO ventilationDTO = ventilationSystemDAO.findByZoneId(zoneId);

      UrgencyState urgencyState = zoneStateMapper.toUrgencyState(zoneStateDTO);
      int smokeConcentration = zoneStateMapper.toSmokeConcentration(zoneStateDTO);
      boolean areAlarmsActive = zoneStateMapper.toAlarmsActive(zoneStateDTO);

      List<Door> doorsWithState = mergeDoorsWithState(structureZone.getDoors(),doorStateDTOs);
      List<Room> roomsWithState = mergeRoomsWithState(structureZone.getRooms());

      List<Agent> agentsDeployed = deployedAgentDTOs.stream().map(agentMapper::toAgent)
          .collect(Collectors.toList());

      List<Agent> agentsArrived = arrivedAgentDTOs.stream().map(agentMapper::toAgent)
          .collect(Collectors.toList());

      List<RequestAgent> agentsRequested = requestedAgentDTOs.stream()
          .map(agentMapper::toRequestAgent).collect(Collectors.toList());

      List<Gathering> gatherings = gatheringDTOs.stream().map(gatheringMapper::toGathering)
          .filter(Objects::nonNull).collect(Collectors.toList());

      VentilationSystem ventilationSystem = ventilationSystemMapper
          .toVentilationSystem(ventilationDTO,buildingVentilationType);

      ZoneFactory zoneFactory = new ZoneFactory();

      return zoneFactory.createZone(zoneId,structureZone.getType(),
          structureZone.getCurrentlyHasActivity(),doorsWithState,roomsWithState,urgencyState,
          smokeConcentration,areAlarmsActive,ventilationSystem,agentsRequested,agentsDeployed,
          agentsArrived,gatherings);
    } catch (SQLException e){
      throw new RuntimeException("Could not merge zone with state: " + zoneId,e);
    }
  }

  private List<Door> mergeDoorsWithState(List<Door> structureDoors,
      List<DoorStateDTO> doorStateDTOs){
    Map<String, DoorStateDTO> doorStateMap = doorStateDTOs.stream()
        .collect(Collectors.toMap(DoorStateDTO::doorId,dto -> dto));

    return structureDoors.stream().map(door -> {
      DoorStateDTO stateDTO = doorStateMap.get(door.getId().toString());
      return doorStateMapper.toDoor(door,stateDTO);
    }).collect(Collectors.toList());
  }

  private List<Room> mergeRoomsWithState(List<Room> structureRooms){
    return structureRooms.stream().map(this::mergeRoomWithState).collect(Collectors.toList());
  }

  private Room mergeRoomWithState(Room structureRoom){
    try{
      RoomId roomId = structureRoom.getId();

      List<AgentDTO> deployedAgentDTOs = agentDAO.findDeployedByRoomId(roomId);
      List<AgentDTO> arrivedAgentDTOs = agentDAO.findArrivedByRoomId(roomId);
      List<AgentDTO> requestedAgentDTOs = agentDAO.findRequestedByRoomId(roomId);

      List<Agent> agentsDeployed = deployedAgentDTOs.stream().map(agentMapper::toAgent)
          .collect(Collectors.toList());

      List<Agent> agentsArrived = arrivedAgentDTOs.stream().map(agentMapper::toAgent)
          .collect(Collectors.toList());

      List<RequestAgent> agentsRequested = requestedAgentDTOs.stream()
          .map(agentMapper::toRequestAgent).collect(Collectors.toList());

      Room roomWithState = RoomFactory.createRoom(structureRoom.getId(),structureRoom.getType(),
          structureRoom.getSupportsElectricClosure(),structureRoom.getLabSafetyResponsible(),
          agentsRequested,agentsDeployed,agentsArrived);

      RoomOccupationDTO occupationDTO = roomOccupationDAO.findByRoomId(roomId);
      List<RoomOccupantDTO> occupantDTOs = roomOccupationDAO.findOccupantsByRoomId(roomId);

      OccupationCounter occupationCounter = roomOccupationMapper.toOccupationCounter(occupationDTO,
          occupantDTOs);
      if (occupationCounter != null){
        roomWithState.setOccupationCounter(occupationCounter);
      }

      return roomWithState;
    } catch (SQLException e){
      throw new RuntimeException("Could not merge room with state: " + structureRoom.getId(),e);
    }
  }

  private void ensureZonesExist(Building building) throws SQLException{
    buildingZoneMappingDAO.insertBuildingIfNotExists(building.getId());

    List<BuildingZoneMappingDTO> mappings = buildingZoneMappingMapper
        .toBuildingZoneMappingDTOs(building.getId(),building.getZones());
    buildingZoneMappingDAO.insertZoneMappingsBatch(mappings);
  }

  private void saveZoneState(Zone zone) throws SQLException{
    ZoneStateDTO stateDTO = zoneStateMapper.toZoneStateDTO(zone);
    zoneStateDAO.insertOrReplace(zone.getId(),stateDTO);
  }

  private void saveZoneAgents(Zone zone) throws SQLException{
    ZoneId zoneId = zone.getId();

    agentDAO.clearDeployedForZone(zoneId);
    List<AgentDTO> deployedDTOs = zone.getAgentsDeployedInZone().stream()
        .map(agent -> agentMapper.toDeployedDTO(agent,zoneId,null)).collect(Collectors.toList());
    agentDAO.insertDeployedBatch(deployedDTOs);

    agentDAO.clearArrivedForZone(zoneId);
    List<AgentDTO> arrivedDTOs = zone.getAgentsArrivedInZone().stream()
        .map(agent -> agentMapper.toArrivedDTO(agent,zoneId,null)).collect(Collectors.toList());
    agentDAO.insertArrivedBatch(arrivedDTOs);

    agentDAO.clearRequestedForZone(zoneId);
    List<AgentDTO> requestedDTOs = zone.getAgentsRequested().stream()
        .map(request -> agentMapper.toRequestedDTO(request,zoneId,null))
        .collect(Collectors.toList());
    agentDAO.insertRequestedBatch(requestedDTOs);
  }

  private void saveZoneGatherings(Zone zone) throws SQLException{
    gatheringDAO.deleteByZoneId(zone.getId());

    List<GatheringDTO> gatheringDTOs = zone.getGatheringsInZone().stream()
        .map(gathering -> gatheringMapper.toGatheringDTO(gathering,zone.getId().toString()))
        .collect(Collectors.toList());

    gatheringDAO.insertBatch(gatheringDTOs);
  }

  private void saveZoneVentilationSystem(Zone zone) throws SQLException{
    ventilationSystemDAO.deleteByZoneId(zone.getId());

    VentilationSystem ventilationSystem = zone.getVentilationSystem();
    if (ventilationSystem != null){
      VentilationSystemDTO dto = ventilationSystemMapper.toVentilationSystemDTO(ventilationSystem,
          zone.getId().toString());
      ventilationSystemDAO.insert(dto);
    }
  }

  private void saveZoneDoors(Zone zone) throws SQLException{
    for (Door door : zone.getDoors()){
      DoorStateDTO doorDTO = doorStateMapper.toDoorStateDTO(door);
      doorStateDAO.insertOrReplace(door.getId().toString(),zone.getId(),doorDTO);
    }
  }

  private void saveRoomsInZone(Zone zone) throws SQLException{
    for (Room room : zone.getRooms()){
      saveRoomState(room,zone.getId());
    }
  }

  private void saveRoomState(Room room,ZoneId zoneId) throws SQLException{
    RoomId roomId = room.getId();

    agentDAO.clearDeployedForRoom(roomId);
    List<AgentDTO> deployedDTOs = room.getAgentsDeployedInRoom().stream()
        .map(agent -> agentMapper.toDeployedDTO(agent,null,roomId)).collect(Collectors.toList());
    agentDAO.insertDeployedBatch(deployedDTOs);

    agentDAO.clearArrivedForRoom(roomId);
    List<AgentDTO> arrivedDTOs = room.getAgentsArrivedInRoom().stream()
        .map(agent -> agentMapper.toArrivedDTO(agent,null,roomId)).collect(Collectors.toList());
    agentDAO.insertArrivedBatch(arrivedDTOs);

    agentDAO.clearRequestedForRoom(roomId);
    List<AgentDTO> requestedDTOs = room.getAgentsRequested().stream()
        .map(request -> agentMapper.toRequestedDTO(request,zoneId,roomId))
        .collect(Collectors.toList());
    agentDAO.insertRequestedBatch(requestedDTOs);

    if (room.supportsOccupationCounting()){
      RoomOccupationDTO occupationDTO = roomOccupationMapper.toOccupationDTO(room);
      roomOccupationDAO.insertOrReplace(occupationDTO);

      roomOccupationDAO.clearOccupantsByRoomId(roomId);
      List<RoomOccupantDTO> occupantDTOs = roomOccupationMapper.toOccupantDTOs(room);
      roomOccupationDAO.insertOccupantsBatch(occupantDTOs);
    }
  }

  private void rollback(){
    try{
      connection.rollback();
    } catch (SQLException rollbackEx){
      throw new RuntimeException("Could not rollback transaction",rollbackEx);
    }
  }

  private void resetAutoCommit(){
    try{
      connection.setAutoCommit(true);
    } catch (SQLException e){
      System.err.println("Could not reset auto-commit: " + e.getMessage());
    }
  }
}
