package ca.ulaval.glo4002.application.infrastructure.repositories.campus;

import static java.lang.String.valueOf;

import ca.ulaval.glo4002.application.domain.access.AccessEvaluator;
import ca.ulaval.glo4002.application.domain.access.accessRules.DoorAccessDuringFireRule;
import ca.ulaval.glo4002.application.domain.access.accessRules.ExtremeNegativePressureRule;
import ca.ulaval.glo4002.application.domain.access.accessRules.ZoneAccessDuringFireRule;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
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
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.campus.rooms.RoomType;
import ca.ulaval.glo4002.application.domain.campus.zones.UrgencyState;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.domain.campus.zones.ZoneType;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.gathering.GatheringId;
import ca.ulaval.glo4002.application.domain.gathering.GatheringType;
import ca.ulaval.glo4002.application.domain.gathering.HighRiskGathering;
import ca.ulaval.glo4002.application.domain.ventilation.AdjustablePressureVentilationSystem;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationSystem;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import ca.ulaval.glo4002.application.infrastructure.factories.DoorFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.RoomFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.ZoneFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BuildingSQLiteTestFixture{

  public static final String TABLE_BUILDINGS = "Buildings";
  public static final String TABLE_ZONES = "Zones";
  public static final String TABLE_DOOR_STATES = "DoorStates";
  public static final String TABLE_AGENT_REQUESTS = "AgentRequests";
  public static final String TABLE_AGENTS_DEPLOYED = "AgentsDeployed";
  public static final String TABLE_GATHERINGS = "Gatherings";
  public static final String TABLE_AGENTS_ARRIVED = "AgentsArrived";
  public static final String TABLE_VENTILATION_SYSTEMS = "VentilationSystems";
  public static final String TABLE_ZONE_STATES = "ZoneStates";
  public static final String TABLE_ROOM_OCCUPATION = "RoomOccupation";
  public static final String TABLE_ROOM_OCCUPANTS = "RoomOccupants";

  public static final BuildingId BUILDING_ID_PLT = new BuildingId("PLT");
  public static final String BUILDING_NAME_PLT = "Pavillon des Technologies";
  public static final PostalAddress BUILDING_POSTAL_ADDRESS_PLT = new PostalAddress(
      "2500 Chemin Polytechnique, Montreal, QC H3T 1J4");
  public static final VentilationSystemType BUILDING_VENTILATION_SYSTEM_PLT = VentilationSystemType.CONTROLLABLE_SPEED;

  public static final ZoneId ZONE_ID_PLT200 = new ZoneId("PLT200");
  public static final ZoneType ZONE_TYPE_HAZARDOUS_MATERIAL = ZoneType.HAZARDOUS_MATERIAL;
  public static final boolean ZONE_HAS_ACTIVITY_TRUE = true;
  public static final boolean ZONE_HAS_ACTIVITY_FALSE = false;
  public static final ZoneFactory zoneFactory = new ZoneFactory();
  public static final int GATHERING_EXPECTED_ATTENDEES = 10;
  public static final Priority PRIORITY_1 = Priority.P1;

  public static final ZoneId ZONE_ID_PLT400 = new ZoneId("PLT400");
  public static final ZoneType ZONE_TYPE_ADMINISTRATION = ZoneType.ADMINISTRATION;
  public static final boolean ZONE_HAS_ACTIVE_ALARMS = true;
  public static final boolean ZONE_HAS_INACTIVE_ALARMS = false;

  public static final RoomId ROOM_ID_PLT200_LAB = new RoomId("PLT200-R001");
  public static final RoomId ROOM_ID_PLT200_OFFICE = new RoomId("PLT200-R002");
  public static final RoomType ROOM_TYPE_LABORATORY = RoomType.LABORATORY;
  public static final RoomType ROOM_TYPE_OFFICE = RoomType.OFFICE;
  public static final boolean ROOM_SUPPORTS_ELECTRIC_CLOSURE_TRUE = true;
  public static final boolean ROOM_SUPPORTS_ELECTRIC_CLOSURE_FALSE = false;
  public static final Idul ROOM_SAFETY_RESPONSIBLE_JDOE = new Idul("1");
  public static final Idul ROOM_SAFETY_RESPONSIBLE_ASMITH = new Idul("2");

  public static final DoorId DOOR_ID_PLT200_COUPE_FEU_01 = new DoorId("PLT200-COUPE-FEU-01");
  public static final DoorId DOOR_ID_PLT200_BARRABLE_01 = new DoorId("PLT200-BARRABLE-01");
  public static final DoorType DOOR_TYPE_FIRE_DOOR = DoorType.FIRE_DOOR;
  public static final DoorType DOOR_TYPE_LOCKABLE = DoorType.LOCKABLE;
  public static final boolean DOOR_ON_EVACUATION_PATH_TRUE = true;
  public static final boolean DOOR_ON_EVACUATION_PATH_FALSE = false;
  public static final boolean DOOR_IS_LOCKED_TRUE = true;
  public static final boolean DOOR_IS_LOCKED_FALSE = false;
  public static final boolean DOOR_IS_OPEN_FALSE = false;
  public static final String AN_IDUL_1 = "jdoe";
  public static final String AN_IDUL_2 = "asmith";
  public static final int ONE_ACCESS = 1;
  public static final int TWO_ACCESSES = 2;
  public static final int TWO_OCCUPANTS = 2;
  public static final int THREE_OCCUPANTS = 3;

  public static final UrgencyState ZONE_STATE_URGENCY_NORMAL = UrgencyState.NORMAL;
  public static final int ZONE_STATE_SMOKE_CONCENTRATION_NONE = 0;

  public static final BuildingId NON_EXISTING_BUILDING_ID = new BuildingId("NON-EXISTING-BUILDING");
  public static final ZoneId NON_EXISTING_ZONE_ID = new ZoneId("NON-EXISTING-ZONE");

  public static final int NUMBER_OF_ZONES_IN_PLT = 1;
  public static final int EMPTY_TABLE = 0;
  public static final int ONE_ROW = 1;

  public static void createTables(Connection connection) throws SQLException{
    String[] createTables = {
        "CREATE TABLE IF NOT EXISTS " + TABLE_BUILDINGS + " ("
            + "building_id TEXT NOT NULL PRIMARY KEY" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_ZONES + " (" + "zone_id TEXT NOT NULL PRIMARY KEY, "
            + "building_id TEXT NOT NULL, " + "FOREIGN KEY (building_id) REFERENCES "
            + TABLE_BUILDINGS + "(building_id)" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_DOOR_STATES + " ("
            + "door_id TEXT NOT NULL PRIMARY KEY, " + "zone_id TEXT NOT NULL, "
            + "is_locked BOOLEAN NOT NULL, " + "initial_locked BOOLEAN NOT NULL, "
            + "is_open BOOLEAN NOT NULL, " + "initial_open BOOLEAN NOT NULL, "
            + "FOREIGN KEY (zone_id) REFERENCES " + TABLE_ZONES + "(zone_id)" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_AGENT_REQUESTS + " ("
            + "intervention_id TEXT NOT NULL PRIMARY KEY, " + "agent_id TEXT, "
            + "priority TEXT NOT NULL, " + "zone_id TEXT NOT NULL, " + "room_id TEXT,"
            + "FOREIGN KEY (zone_id) REFERENCES " + TABLE_ZONES + "(zone_id)" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_AGENTS_DEPLOYED + " ("
            + "intervention_id TEXT NOT NULL PRIMARY KEY, " + "agent_id TEXT, "
            + "priority TEXT NOT NULL, " + "zone_id TEXT, " + "room_id TEXT,"
            + "FOREIGN KEY (zone_id) REFERENCES " + TABLE_ZONES + "(zone_id)" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_AGENTS_ARRIVED + " ("
            + "intervention_id TEXT NOT NULL PRIMARY KEY, " + "agent_id TEXT, "
            + "priority TEXT NOT NULL, " + "zone_id TEXT, " + "room_id TEXT,"
            + "FOREIGN KEY (zone_id) REFERENCES " + TABLE_ZONES + "(zone_id)" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_GATHERINGS + " ("
            + "gathering_id TEXT NOT NULL PRIMARY KEY, "
            + "expected_attendees INTEGER DEFAULT 0 NOT NULL, " + "gathering_type TEXT NOT NULL, "
            + "zone_id TEXT, " + "FOREIGN KEY (zone_id) REFERENCES " + TABLE_ZONES + "(zone_id)"
            + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_VENTILATION_SYSTEMS + " ("
            + "zone_id TEXT NOT NULL PRIMARY KEY, " + "vitesse_retour INTEGER, "
            + "vitesse_distribution INTEGER, " + "is_open INTEGER, "
            + "FOREIGN KEY (zone_id) REFERENCES " + TABLE_ZONES + "(zone_id)" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_ZONE_STATES + " ("
            + "zone_id TEXT NOT NULL PRIMARY KEY, "
            + "urgency_state TEXT DEFAULT 'AUCUN' NOT NULL, "
            + "smoke_concentration INTEGER DEFAULT 0 NOT NULL, "
            + "are_alarms_active INTEGER DEFAULT false NOT NULL, "
            + "FOREIGN KEY (zone_id) REFERENCES " + TABLE_ZONES + "(zone_id)" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_ROOM_OCCUPATION + " ("
            + "room_id TEXT NOT NULL PRIMARY KEY, "
            + "current_occupation INTEGER NOT NULL DEFAULT 0, "
            + "supports_counting BOOLEAN NOT NULL DEFAULT 0" + ")",
        "CREATE TABLE IF NOT EXISTS " + TABLE_ROOM_OCCUPANTS + " (" + "room_id TEXT NOT NULL, "
            + "idul TEXT NOT NULL, " + "consecutive_accesses INTEGER NOT NULL DEFAULT 1, "
            + "PRIMARY KEY (room_id, idul), " + "FOREIGN KEY (room_id) REFERENCES "
            + TABLE_ROOM_OCCUPATION + "(room_id) ON DELETE CASCADE" + ")"};

    try (Statement statement = connection.createStatement()){
      for (String createTable : createTables){
        statement.execute(createTable);
      }
    }
  }

  public static void insertStateDataOnly(Connection connection) throws SQLException{
    try (Statement statement = connection.createStatement()){
      statement.execute(buildInsertBuildingIdQuery());
      statement.execute(buildInsertZoneIdQuery(valueOf(ZONE_ID_PLT200)));

      statement.execute(buildInsertZoneStateQuery(valueOf(ZONE_ID_PLT200)));

      statement.execute(buildInsertDoorStateQuery(valueOf(DOOR_ID_PLT200_COUPE_FEU_01),
          valueOf(ZONE_ID_PLT200),DOOR_IS_LOCKED_FALSE,DOOR_IS_LOCKED_FALSE));

      statement.execute(buildInsertDoorStateQuery(valueOf(DOOR_ID_PLT200_BARRABLE_01),
          valueOf(ZONE_ID_PLT200),DOOR_IS_LOCKED_TRUE,DOOR_IS_LOCKED_TRUE));
    }
  }

  private static String buildInsertBuildingIdQuery(){
    return String.format("INSERT INTO %s (building_id) VALUES ('%s')",TABLE_BUILDINGS,
        BUILDING_ID_PLT);
  }

  private static String buildInsertZoneIdQuery(String zoneId){
    return String.format("INSERT INTO %s (zone_id, building_id) VALUES ('%s', '%s')",TABLE_ZONES,
        zoneId,BUILDING_ID_PLT);
  }

  private static String buildInsertZoneStateQuery(String zoneId){
    return String.format("INSERT INTO %s VALUES ('%s', '%s', %d, %d)",TABLE_ZONE_STATES,zoneId,
        ZONE_STATE_URGENCY_NORMAL,ZONE_STATE_SMOKE_CONCENTRATION_NONE,
        ZONE_HAS_INACTIVE_ALARMS ? 1 : 0);
  }

  private static String buildInsertDoorStateQuery(String doorId,String zoneId,boolean isLocked,
      boolean initialLocked){
    return String.format("INSERT INTO %s VALUES ('%s', '%s', %d, %d, %d, %d)",TABLE_DOOR_STATES,
        doorId,zoneId,isLocked ? 1 : 0,initialLocked ? 1 : 0,
        BuildingSQLiteTestFixture.DOOR_IS_OPEN_FALSE ? 1 : 0,
        BuildingSQLiteTestFixture.DOOR_IS_OPEN_FALSE ? 1 : 0);
  }

  public static int getTableRowCount(Connection connection,String tableName) throws SQLException{
    return switch (tableName){
      case "Buildings" -> countFromTable(connection,"Buildings");
      case "Zones" -> countFromTable(connection,"Zones");
      case "DoorStates" -> countFromTable(connection,"DoorStates");
      case "ZoneStates" -> countFromTable(connection,"ZoneStates");
      case "AgentRequests" -> countFromTable(connection,"AgentRequests");
      case "AgentsDeployed" -> countFromTable(connection,"AgentsDeployed");
      case "AgentsArrived" -> countFromTable(connection,"AgentsArrived");
      case "VentilationSystems" -> countFromTable(connection,"VentilationSystems");
      case "RoomOccupation" -> countFromTable(connection,"RoomOccupation");
      case "RoomOccupants" -> countFromTable(connection,"RoomOccupants");
      default -> throw new IllegalArgumentException("Invalid table name: " + tableName);
    };
  }

  private static int countFromTable(Connection connection,String tableName) throws SQLException{
    String query = "SELECT COUNT(*) FROM " + tableName;
    try (PreparedStatement statement = connection.prepareStatement(query);
        ResultSet rs = statement.executeQuery()){
      rs.next();
      return rs.getInt(1);
    }
  }

  public static Zone findZoneById(Building building,ZoneId zoneId){
    return building.getZones().stream().filter(zone -> zone.getId().equals(zoneId)).findFirst()
        .orElseThrow(() -> new AssertionError("Zone not found: " + zoneId));
  }

  public static Room findRoomById(Zone zone,RoomId roomId){
    return zone.getRooms().stream().filter(room -> room.getId().equals(roomId)).findFirst()
        .orElseThrow(() -> new AssertionError("Room not found: " + roomId));
  }

  public static Door findDoorById(Zone zone,DoorId doorId){
    return zone.getDoors().stream().filter(door -> door.getId().equals(doorId)).findFirst()
        .orElseThrow(() -> new AssertionError("Door not found: " + doorId));
  }

  public static Campus getNewCampus(){
    return new Campus(){
      @Override
      public List<Building> getBuildings(){
        return List.of(getTestBuildingPLT());
      }
    };
  }

  public static Campus getCampusWithNewZone(){
    return new Campus(){
      @Override
      public List<Building> getBuildings(){
        return List.of(getTestBuildingPLTWithNewZone());
      }
    };
  }

  public static Building getExpectedBuildingPLT(){
    return getTestBuildingPLT();
  }

  private static Building getTestBuildingPLT(){
    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(getTestZonePLT200()),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getExpectedBuildingWithNewZone(){
    return getTestBuildingPLTWithNewZone();
  }

  private static Building getTestBuildingPLTWithNewZone(){
    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(getTestZonePLT200(),getTestZonePLT400()),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  private static Zone getTestZonePLT200(){
    return zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getTestRoomLabPLT(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>());
  }

  private static Zone getTestZonePLT400(){
    return zoneFactory.createZone(ZONE_ID_PLT400,ZONE_TYPE_ADMINISTRATION,ZONE_HAS_ACTIVITY_FALSE,
        new ArrayList<>(),new ArrayList<>(),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>());
  }

  private static Door getTestFireDoorPLT(){
    return DoorFactory.createDoor(DOOR_ID_PLT200_COUPE_FEU_01,DOOR_TYPE_FIRE_DOOR,
        DOOR_ON_EVACUATION_PATH_FALSE,DOOR_IS_LOCKED_FALSE,DOOR_IS_OPEN_FALSE,DOOR_IS_LOCKED_FALSE,
        DOOR_IS_OPEN_FALSE);
  }

  private static Door getTestLockableDoorPLT(){
    return DoorFactory.createDoor(DOOR_ID_PLT200_BARRABLE_01,DOOR_TYPE_LOCKABLE,
        DOOR_ON_EVACUATION_PATH_TRUE,DOOR_IS_LOCKED_TRUE,DOOR_IS_OPEN_FALSE,DOOR_IS_LOCKED_TRUE,
        DOOR_IS_OPEN_FALSE);
  }

  private static Room getTestRoomLabPLT(){
    return RoomFactory.createRoom(ROOM_ID_PLT200_LAB,ROOM_TYPE_LABORATORY,
        ROOM_SUPPORTS_ELECTRIC_CLOSURE_TRUE,ROOM_SAFETY_RESPONSIBLE_JDOE,new ArrayList<>(),
        new ArrayList<>(),new ArrayList<>());
  }

  private static Room getTestRoomOfficePLT(){
    return RoomFactory.createRoom(ROOM_ID_PLT200_OFFICE,ROOM_TYPE_OFFICE,
        ROOM_SUPPORTS_ELECTRIC_CLOSURE_FALSE,ROOM_SAFETY_RESPONSIBLE_ASMITH,new ArrayList<>(),
        new ArrayList<>(),new ArrayList<>());
  }

  private static Room getRoomLabPLTWithAgentRequests(){
    List<RequestAgent> agentRequests = new ArrayList<>();
    addAgentRequestsInList(1,agentRequests);
    return RoomFactory.createRoom(ROOM_ID_PLT200_LAB,ROOM_TYPE_LABORATORY,
        ROOM_SUPPORTS_ELECTRIC_CLOSURE_TRUE,ROOM_SAFETY_RESPONSIBLE_JDOE,agentRequests,
        new ArrayList<>(),new ArrayList<>());
  }

  private static Room getRoomLabPLTWithAgentsDeployed(){
    List<Agent> agentsDeployed = new ArrayList<>();
    addAgentsDeployedInList(1,agentsDeployed);
    return RoomFactory.createRoom(ROOM_ID_PLT200_LAB,ROOM_TYPE_LABORATORY,
        ROOM_SUPPORTS_ELECTRIC_CLOSURE_TRUE,ROOM_SAFETY_RESPONSIBLE_JDOE,new ArrayList<>(),
        agentsDeployed,new ArrayList<>());
  }

  private static Room getRoomLabPLTWithAgentsArrived(){
    List<Agent> agentsArrived = new ArrayList<>();
    addAgentsArrivedInList(1,agentsArrived);
    return RoomFactory.createRoom(ROOM_ID_PLT200_LAB,ROOM_TYPE_LABORATORY,
        ROOM_SUPPORTS_ELECTRIC_CLOSURE_TRUE,ROOM_SAFETY_RESPONSIBLE_JDOE,new ArrayList<>(),
        new ArrayList<>(),agentsArrived);
  }

  public static Building getBuildingPLTWithModifiedZoneState(UrgencyState urgencyState,
      int smokeConcentration,boolean hasActiveAlarms){
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getTestRoomLabPLT(),getTestRoomOfficePLT()),urgencyState,smokeConcentration,
        hasActiveAlarms,createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>(),new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedDoorState(boolean isLocked,boolean isOpen){
    Door modifiedDoor = DoorFactory.createDoor(DOOR_ID_PLT200_COUPE_FEU_01,DOOR_TYPE_FIRE_DOOR,
        DOOR_ON_EVACUATION_PATH_FALSE,isLocked,isOpen,DOOR_IS_LOCKED_FALSE,DOOR_IS_OPEN_FALSE);
    Zone zone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(modifiedDoor,getTestLockableDoorPLT()),
        List.of(getTestRoomLabPLT(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(zone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedAgentsDeployedInZone(int agentCount){
    List<Agent> agentsInZone = new ArrayList<>();

    addAgentsDeployedInList(agentCount,agentsInZone);
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getTestRoomLabPLT(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),agentsInZone,new ArrayList<>(),
        new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedAgentsArrivedInZone(int agentCount){
    List<Agent> agentsArrivedInZone = new ArrayList<>();

    addAgentsArrivedInList(agentCount,agentsArrivedInZone);
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getTestRoomLabPLT(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),agentsArrivedInZone,
        new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedAgentsRequestsInZone(int agentCount){
    List<RequestAgent> agentRequests = new ArrayList<>();
    addAgentRequestsInList(agentCount,agentRequests);
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getTestRoomLabPLT(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),agentRequests,new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedGatheringsInZone(int gatheringCount){
    List<Gathering> gatheringsInZone = new ArrayList<>();
    addGatheringsInList(gatheringCount,gatheringsInZone);
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getTestRoomLabPLT(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),
        gatheringsInZone);

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedAgentsRequestsInRoom(){
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getRoomLabPLTWithAgentRequests(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedAgentsDeployedInRoom(){
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getRoomLabPLTWithAgentsDeployed(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  public static Building getBuildingPLTWithModifiedAgentsArrivedInRoom(){
    Zone modifiedZone = zoneFactory.createZone(ZONE_ID_PLT200,ZONE_TYPE_HAZARDOUS_MATERIAL,
        ZONE_HAS_ACTIVITY_TRUE,List.of(getTestFireDoorPLT(),getTestLockableDoorPLT()),
        List.of(getRoomLabPLTWithAgentsArrived(),getTestRoomOfficePLT()),ZONE_STATE_URGENCY_NORMAL,
        ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_INACTIVE_ALARMS,
        createDefaultVentilationSystem(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>());

    return new Building(BUILDING_ID_PLT,BUILDING_NAME_PLT,BUILDING_POSTAL_ADDRESS_PLT,
        BUILDING_VENTILATION_SYSTEM_PLT,List.of(modifiedZone),
        new AccessEvaluator(Arrays.asList(new DoorAccessDuringFireRule(),
            new ZoneAccessDuringFireRule(),new ExtremeNegativePressureRule())));
  }

  private static VentilationSystem createDefaultVentilationSystem(){

    return switch (BuildingSQLiteTestFixture.BUILDING_VENTILATION_SYSTEM_PLT){
      case CONTROLLABLE_SPEED -> new AdjustablePressureVentilationSystem();
      case SIMPLE -> new SimpleVentilationSystem();
    };
  }

  private static void addAgentRequestsInList(int agentCount,List<RequestAgent> agentRequests){
    InterventionId interventionId = new InterventionId();
    AgentId agentId = new AgentId();
    for (int i = 0; i < agentCount; i++){
      agentRequests.add(new RequestAgent(agentId,ZONE_ID_PLT200,PRIORITY_1,interventionId));
    }
  }

  private static void addAgentsDeployedInList(int agentCount,List<Agent> agentsInZone){
    for (int i = 0; i < agentCount; i++){
      InterventionId interventionId = new InterventionId();
      AgentId agentId = new AgentId();
      agentsInZone.add(new Agent(agentId,interventionId,PRIORITY_1));
    }
  }

  private static void addGatheringsInList(int gatheringCount,List<Gathering> gatheringsInZone){
    for (int i = 0; i < gatheringCount; i++){
      GatheringId gatheringId = new GatheringId();
      gatheringsInZone.add(
          new HighRiskGathering(gatheringId,GATHERING_EXPECTED_ATTENDEES,GatheringType.PROTEST));
    }
  }

  private static void addAgentsArrivedInList(int agentCount,List<Agent> agentsArrivedInZone){
    for (int i = 0; i < agentCount; i++){
      InterventionId interventionId = new InterventionId();
      AgentId agentId = new AgentId();
      agentsArrivedInZone.add(new Agent(agentId,interventionId,PRIORITY_1));
    }
  }
}
