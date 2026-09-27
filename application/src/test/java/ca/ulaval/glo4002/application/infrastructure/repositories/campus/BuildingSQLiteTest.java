package ca.ulaval.glo4002.application.infrastructure.repositories.campus;

import static ca.ulaval.glo4002.application.infrastructure.repositories.campus.BuildingSQLiteTestFixture.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.campus.Campus;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.campus.zones.UrgencyState;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import ca.ulaval.glo4002.application.infrastructure.exceptions.BuildingNotFoundException;
import ca.ulaval.glo4002.application.infrastructure.exceptions.ZoneNotFoundException;
import ca.ulaval.glo4002.application.infrastructure.repositories.BuildingSQLite;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BuildingSQLiteTest{

  private Connection connection;
  private BuildingSQLite repository;
  private CampusLoaderFromAPI campusLoader;

  @BeforeEach
  void createSchema() throws SQLException{
    connection = DriverManager.getConnection("jdbc:sqlite::memory:");
    campusLoader = mock(CampusLoaderFromAPI.class);
    repository = new BuildingSQLite(connection,campusLoader);
    createTables(connection);
    givenCampusLoaderReturnsTestCampus();
  }

  @AfterEach
  void tearDown() throws SQLException{
    if (connection != null && !connection.isClosed()){
      connection.close();
    }
  }

  @Nested
  class FindBuildingById{

    @Test
    void givenExistingBuildingInDatabase_whenFindBuildingById_thenReturnsBuildingWithCorrectProperties()
        throws SQLException{
      insertStateDataOnly(connection);

      Building result = repository.findBuildingById(BUILDING_ID_PLT);

      Building buildingPLT = getExpectedBuildingPLT();
      assertBuildingsAreEqual(buildingPLT,result);
    }

    @Test
    void givenNonExistingBuilding_whenFindBuildingById_thenThrowsBuildingNotFoundException(){
      assertThrows(BuildingNotFoundException.class,
          () -> repository.findBuildingById(NON_EXISTING_BUILDING_ID));
    }
  }

  @Nested
  class GetParentBuilding{

    @Test
    void givenExistingZone_whenGetParentBuilding_thenReturnsParentBuilding() throws SQLException{
      givenCampusLoaderReturnsTestCampus();
      insertStateDataOnly(connection);

      Building result = repository.getParentBuilding(ZONE_ID_PLT200);

      Building expectedBuilding = getExpectedBuildingPLT();
      assertBuildingsAreEqual(expectedBuilding,result);
    }

    @Test
    void givenNonExistingZoneInDatabase_whenGetParentBuilding_thenRehydratesAndReturnsParentBuilding(){
      givenCampusLoaderReturnsCampusWithNewZone();

      Building result = repository.getParentBuilding(ZONE_ID_PLT400);

      Building expectedBuilding = getExpectedBuildingWithNewZone();
      assertBuildingsAreEqual(expectedBuilding,result);
    }

    @Test
    void givenNonExistingZoneInAPI_whenGetParentBuilding_thenThrowsZoneNotFoundException(){
      givenCampusLoaderReturnsTestCampus();

      assertThrows(ZoneNotFoundException.class,
          () -> repository.getParentBuilding(NON_EXISTING_ZONE_ID));
    }
  }

  @Nested
  class SaveBuildingState{

    @BeforeEach
    public void setCampusLoaderReturnValue(){
      givenCampusLoaderReturnsTestCampus();
    }

    @Test
    void givenModifiedZoneUrgencyState_whenSaveBuildingState_thenPersistsNewUrgencyState()
        throws SQLException{
      insertStateDataOnly(connection);
      UrgencyState newUrgencyState = UrgencyState.FIRE;
      Building modifiedBuilding = getBuildingPLTWithModifiedZoneState(newUrgencyState,
          ZONE_STATE_SMOKE_CONCENTRATION_NONE,ZONE_HAS_ACTIVE_ALARMS);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenModifiedSmokeConcentration_whenSaveBuildingState_thenPersistsNewSmokeConcentration()
        throws SQLException{
      insertStateDataOnly(connection);
      int newSmokeConcentration = 5;
      Building modifiedBuilding = getBuildingPLTWithModifiedZoneState(ZONE_STATE_URGENCY_NORMAL,
          newSmokeConcentration,ZONE_HAS_INACTIVE_ALARMS);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenModifiedDoorLockedState_whenSaveBuildingState_thenPersistsNewLockedState()
        throws SQLException{
      insertStateDataOnly(connection);
      boolean newLockedState = true;
      boolean initialLockedState = false;
      Building modifiedBuilding = getBuildingPLTWithModifiedDoorState(newLockedState,
          initialLockedState);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenModifiedDoorOpenState_whenSaveBuildingState_thenPersistsNewOpenState()
        throws SQLException{
      insertStateDataOnly(connection);
      boolean newOpenState = true;
      boolean initialOpenState = false;
      Building modifiedBuilding = getBuildingPLTWithModifiedDoorState(initialOpenState,
          newOpenState);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenNewDeployedAgentsInZone_whenSaveBuildingState_thenPersistsNewAgentsArrivedInZone()
        throws SQLException{
      insertStateDataOnly(connection);
      int newAgentCount = 4;
      Building modifiedBuilding = getBuildingPLTWithModifiedAgentsArrivedInZone(newAgentCount);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenNewDeployedAgentsInZone_whenSaveBuildingState_thenPersistsNewAgentsDeployedInZone()
        throws SQLException{
      insertStateDataOnly(connection);
      int newAgentCount = 4;
      Building modifiedBuilding = getBuildingPLTWithModifiedAgentsDeployedInZone(newAgentCount);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenNewAgentRequestsInZone_whenSaveBuildingState_thenPersistsNewAgentRequestsInZone()
        throws SQLException{
      insertStateDataOnly(connection);
      int newAgentRequestCount = 1;
      Building modifiedBuilding = getBuildingPLTWithModifiedAgentsRequestsInZone(
          newAgentRequestCount);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenNewArrivedAgentsInRoom_whenSaveBuildingState_thenPersistsNewAgentsInRoom(){
      Building modifiedBuilding = getBuildingPLTWithModifiedAgentsArrivedInRoom();

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenNewGatheringsInZone_whenSaveBuildingState_thenPersistsGatheringsInZone()
        throws SQLException{
      insertStateDataOnly(connection);
      int newGatheringsCount = 3;
      Building modifiedBuilding = getBuildingPLTWithModifiedGatheringsInZone(newGatheringsCount);

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenNewDeployedAgentsInRoom_whenSaveBuildingState_thenPersistsNewAgentsInRoom(){
      Building modifiedBuilding = getBuildingPLTWithModifiedAgentsDeployedInRoom();

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenNewAgentRequestsInRoom_whenSaveBuildingState_thenPersistsNewAgentsInRoom(){
      Building modifiedBuilding = getBuildingPLTWithModifiedAgentsRequestsInRoom();

      repository.saveBuildingState(modifiedBuilding);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(modifiedBuilding,savedBuilding);
    }

    @Test
    void givenRoomWithOccupation_whenSaveBuildingState_thenPersistsOccupationData()
        throws SQLException{
      insertStateDataOnly(connection);
      Building building = getExpectedBuildingPLT();
      Zone zone = findZoneById(building,ZONE_ID_PLT200);
      Room room = findRoomById(zone,ROOM_ID_PLT200_LAB);
      room.incrementIdentifiedUserOccupation(new Idul(AN_IDUL_1));
      room.incrementIdentifiedUserOccupation(new Idul(AN_IDUL_2));
      room.incrementUnidentifiedUserOccupation();

      repository.saveBuildingState(building);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      assertBuildingsAreEqual(building,savedBuilding);
    }

    @Test
    void givenRoomWithIdentifiedOccupants_whenSaveBuildingState_thenPersistsConsecutiveAccesses()
        throws SQLException{
      insertStateDataOnly(connection);
      Building building = getExpectedBuildingPLT();
      Zone zone = findZoneById(building,ZONE_ID_PLT200);
      Room room = findRoomById(zone,ROOM_ID_PLT200_LAB);
      room.incrementIdentifiedUserOccupation(new Idul(AN_IDUL_1));
      room.incrementIdentifiedUserOccupation(new Idul(AN_IDUL_1));
      room.incrementIdentifiedUserOccupation(new Idul(AN_IDUL_2));

      repository.saveBuildingState(building);

      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      Zone savedZone = findZoneById(savedBuilding,ZONE_ID_PLT200);
      Room savedRoom = findRoomById(savedZone,ROOM_ID_PLT200_LAB);

      assertEquals(THREE_OCCUPANTS,savedRoom.getCurrentOccupation());
      assertEquals(TWO_ACCESSES,
          savedRoom.getConsecutiveAccessesByPerson().get(new Idul(AN_IDUL_1)));
      assertEquals(ONE_ACCESS,savedRoom.getConsecutiveAccessesByPerson().get(new Idul(AN_IDUL_2)));
    }

    @Test
    void givenRoomWithOnlyUnidentifiedOccupants_whenSaveBuildingState_thenPersistsCountOnly()
        throws SQLException{
      insertStateDataOnly(connection);
      Building building = getExpectedBuildingPLT();
      Zone zone = findZoneById(building,ZONE_ID_PLT200);
      Room room = findRoomById(zone,ROOM_ID_PLT200_LAB);
      room.incrementUnidentifiedUserOccupation();
      room.incrementUnidentifiedUserOccupation();

      repository.saveBuildingState(building);

      assertEquals(ONE_ROW,getTableRowCount(connection,TABLE_ROOM_OCCUPATION));
      assertEquals(EMPTY_TABLE,getTableRowCount(connection,TABLE_ROOM_OCCUPANTS));
      Building savedBuilding = repository.findBuildingById(BUILDING_ID_PLT);
      Zone savedZone = findZoneById(savedBuilding,ZONE_ID_PLT200);
      Room savedRoom = findRoomById(savedZone,ROOM_ID_PLT200_LAB);
      assertEquals(TWO_OCCUPANTS,savedRoom.getCurrentOccupation());
      assertTrue(savedRoom.getConsecutiveAccessesByPerson().isEmpty());
    }

    @Test
    void givenRoomWithoutOccupationSupport_whenSaveBuildingState_thenDoesNotPersistOccupation()
        throws SQLException{
      insertStateDataOnly(connection);
      Building building = getExpectedBuildingPLT();

      repository.saveBuildingState(building);

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,TABLE_ROOM_OCCUPATION));
      assertEquals(EMPTY_TABLE,getTableRowCount(connection,TABLE_ROOM_OCCUPANTS));
    }

    @Test
    void givenNewBuilding_whenSaveBuildingState_thenCreatesZoneRecordsInDatabase()
        throws SQLException{
      Building building = getExpectedBuildingPLT();

      repository.saveBuildingState(building);

      assertEquals(ONE_ROW,getTableRowCount(connection,"Buildings"));
      assertEquals(NUMBER_OF_ZONES_IN_PLT,getTableRowCount(connection,"Zones"));
    }
  }

  @Nested
  class Clear{
    @BeforeEach
    public void insertCampus() throws SQLException{
      givenCampusLoaderReturnsTestCampus();
      insertStateDataOnly(connection);
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenBuildingsTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"Buildings"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenZonesTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"Zones"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenAgentRequestsTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"AgentRequests"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenAgentsDeployedTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"AgentsDeployed"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenAgentsArrivedTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"AgentsArrived"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenZoneStatesTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"ZoneStates"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenDoorStatesTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"DoorStates"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenVentilationSystemsTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"VentilationSystems"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenRoomOccupationTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"RoomOccupation"));
    }

    @Test
    void givenPopulatedDatabase_whenClear_thenRoomOccupantsTableIsEmpty() throws SQLException{
      repository.clear();

      assertEquals(EMPTY_TABLE,getTableRowCount(connection,"RoomOccupants"));
    }
  }

  @Nested
  class ErrorHandling{

    @Test
    void givenBrokenDatabaseConnection_whenFindBuildingById_thenThrowsRuntimeException()
        throws SQLException{
      Connection brokenConnection = mock(Connection.class);
      when(brokenConnection.prepareStatement(anyString()))
          .thenThrow(new SQLException("Connection failed"));
      BuildingSQLite brokenRepository = new BuildingSQLite(brokenConnection,campusLoader);

      assertThrows(RuntimeException.class,() -> brokenRepository.findBuildingById(BUILDING_ID_PLT));
    }
  }

  private void givenCampusLoaderReturnsTestCampus(){
    Campus campus = getNewCampus();
    when(campusLoader.loadCampus()).thenReturn(campus);
  }

  private void givenCampusLoaderReturnsCampusWithNewZone(){
    Campus campus = getCampusWithNewZone();
    when(campusLoader.loadCampus()).thenReturn(campus);
  }

  private void assertBuildingsAreEqual(Building expected,Building actual){
    assertEquals(expected.getId(),actual.getId(),"Building ID should match");
    assertEquals(expected.getName(),actual.getName(),"Building name should match");
    assertEquals(expected.getPostalAddress(),actual.getPostalAddress(),
        "Building postal address should match");
    assertEquals(expected.getVentilationSystem(),actual.getVentilationSystem(),
        "Building ventilation system should match");
    assertEquals(expected.getZones().size(),actual.getZones().size(),
        "Number of zones should match");

    for (Zone expectedZone : expected.getZones()){
      Zone actualZone = findZoneById(actual,expectedZone.getId());
      assertZonesAreEqual(expectedZone,actualZone);
    }
  }

  private void assertZonesAreEqual(Zone expected,Zone actual){
    assertEquals(expected.getId(),actual.getId(),"Zone ID should match");
    assertEquals(expected.getType(),actual.getType(),"Zone type should match");
    assertEquals(expected.getCurrentlyHasActivity(),actual.getCurrentlyHasActivity(),
        "Zone activity state should match");
    assertEquals(expected.getUrgencyState(),actual.getUrgencyState(),
        "Zone urgency state should match");
    assertEquals(expected.getSmokeConcentration(),actual.getSmokeConcentration(),
        "Zone smoke concentration should match");
    assertEquals(expected.getAgentsDeployedInZone(),actual.getAgentsDeployedInZone(),
        "Number of Agents in zone should match");
    assertEquals(expected.getNumberOfAgentsRequested(),actual.getNumberOfAgentsRequested(),
        "Number of agents requested should match");
    assertEquals(expected.getGatheringsInZone(),actual.getGatheringsInZone(),
        "Gatherings should match");

    assertAgentRequestsAreEqual(expected.getAgentsRequested(),actual.getAgentsRequested());

    assertVentilationSystemsEqual(expected.getVentilationSystem(),actual.getVentilationSystem());

    for (Door expectedDoor : expected.getDoors()){
      Door actualDoor = findDoorById(actual,expectedDoor.getId());
      assertDoorsAreEqual(expectedDoor,actualDoor);
    }

    for (Room expectedRoom : expected.getRooms()){
      Room actualRoom = findRoomById(actual,expectedRoom.getId());
      assertRoomsAreEqual(expectedRoom,actualRoom);
    }
  }

  private void assertDoorsAreEqual(Door expected,Door actual){
    assertEquals(expected.getId(),actual.getId(),"Door ID should match");
    assertEquals(expected.getType(),actual.getType(),"Door type should match");
    assertEquals(expected.getOnEvacuationPath(),actual.getOnEvacuationPath(),
        "Door evacuation path status should match");
    assertEquals(expected.getIsLocked(),actual.getIsLocked(),"Door locked state should match");
    assertEquals(expected.getIsOpen(),actual.getIsOpen(),"Door open state should match");
  }

  private void assertRoomsAreEqual(Room expected,Room actual){
    assertEquals(expected.getId(),actual.getId(),"Room ID should match");
    assertEquals(expected.getType(),actual.getType(),"Room type should match");
    assertEquals(expected.getSupportsElectricClosure(),actual.getSupportsElectricClosure(),
        "Room electric closure support should match");
    assertEquals(expected.getLabSafetyResponsible(),actual.getLabSafetyResponsible(),
        "Room lab safety responsible should match");
    assertEquals(expected.getAgentsDeployedInRoom(),actual.getAgentsDeployedInRoom(),
        "Number of Agents in room should match");
    assertEquals(expected.getAgentsRequested(),actual.getAgentsRequested(),
        "Number of agents requested should match");

    assertAgentRequestsAreEqual(expected.getAgentsRequested(),actual.getAgentsRequested());
    assertEquals(expected.getCurrentOccupation(),actual.getCurrentOccupation(),
        "Room occupation should match");
  }

  private void assertVentilationSystemsEqual(VentilationSystem expected,VentilationSystem actual){
    Map<String, String> expectedState = expected.getCurrentState();
    Map<String, String> actualState = actual.getCurrentState();
    assertEquals(expectedState,actualState,"Ventilation system states should match");
  }

  private void assertAgentRequestsAreEqual(List<RequestAgent> expected,List<RequestAgent> actual){
    assertEquals(expected.size(),actual.size(),"Number of agent requests should match");

    for (RequestAgent expectedRequest : expected){
      assertTrue(actual.contains(expectedRequest),
          "Zone should contain agent request: " + expectedRequest);
    }
  }
}
