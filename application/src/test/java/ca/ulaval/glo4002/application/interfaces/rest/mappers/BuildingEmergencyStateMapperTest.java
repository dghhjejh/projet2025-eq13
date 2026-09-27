package ca.ulaval.glo4002.application.interfaces.rest.mappers;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.zones.HazardousMaterialZone;
import ca.ulaval.glo4002.application.domain.campus.zones.UrgencyState;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import ca.ulaval.glo4002.application.interfaces.rest.responses.AdjustablePressureVentilationZoneResponse;
import ca.ulaval.glo4002.application.interfaces.rest.responses.BuildingEmergencyStateResponse;
import ca.ulaval.glo4002.application.interfaces.rest.responses.SimpleVentilationZoneResponse;
import ca.ulaval.glo4002.application.interfaces.rest.responses.ZoneResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BuildingEmergencyStateMapperTest{

  private static final BuildingId BUILDING_ID = new BuildingId("PLT");
  private static final ZoneId ZONE_1_ID = new ZoneId("PLT200");
  private static final ZoneId ZONE_2_ID = new ZoneId("PLT300");
  private static final DoorId DOOR_1_ID = new DoorId("PLT200-BARRABLE");
  private static final DoorId DOOR_2_ID = new DoorId("PLT200-COUPE-FEU");

  private static final int NO_SMOKE = 0;
  private static final int NON_NULL_SMOKE_CONCENTRATION = 10;
  private static final int NO_AGENTS = 0;
  private static final int TWO_AGENTS = 2;
  private static final int THREE_AGENTS = 3;
  private static final int TWO_DOORS = 2;
  private static final int ONE_ZONE = 1;
  private static final int TWO_ZONES = 2;
  private static final boolean IS_OPEN = true;
  private static final boolean IS_LOCKED = true;

  private static final double ADJUSTABLE_DISTRIBUTION_SPEED = 50.0;
  private static final double ADJUSTABLE_RETURN_SPEED = 45.0;
  private static final String VENTILATION_OPEN = "ouverte";
  private static final String VENTILATION_CLOSED = "fermée";

  private Building building;
  private HazardousMaterialZone zone1;
  private HazardousMaterialZone zone2;
  private Door door1;
  private Door door2;
  private VentilationSystem adjustableVentilation;
  private VentilationSystem simpleVentilation;

  @BeforeEach
  void setUp(){
    building = mock(Building.class);
    zone1 = mock(HazardousMaterialZone.class);
    zone2 = mock(HazardousMaterialZone.class);
    door1 = mock(Door.class);
    door2 = mock(Door.class);
    adjustableVentilation = mock(VentilationSystem.class);
    simpleVentilation = mock(VentilationSystem.class);
    when(building.getId()).thenReturn(BUILDING_ID);
  }

  @Nested
  class BuildingMapping{

    @Test
    void givenBuilding_whenMappingToResponse_thenReturnsBuildingId(){
      givenBuildingWithZones();

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertEquals(BUILDING_ID.toString(),response.id());
    }

    @Test
    void givenEmptyBuilding_whenMappingToResponse_thenReturnsEmptyZones(){
      givenEmptyBuilding();

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertTrue(response.zones().isEmpty());
    }

    @Test
    void givenEmptyBuilding_whenMappingToResponse_thenReturnsEmptyDoors(){
      givenEmptyBuilding();

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertTrue(response.portes().isEmpty());
    }

    @Test
    void givenSingleZone_whenMappingToResponse_thenReturnsOneZone(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertEquals(ONE_ZONE,response.zones().size());
    }
  }

  @Nested
  class AgentAggregation{

    @Test
    void givenZoneWithNoAgents_whenMappingToResponse_thenReturnsZeroAgents(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertEquals(NO_AGENTS,response.agents().demandés());
    }

    @Test
    void givenMultipleZonesWithAgents_whenMappingToResponse_thenAggregatesAllAgents(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,TWO_AGENTS,NO_SMOKE);
      givenZoneWithAdjustableVentilation(zone2,ZONE_2_ID,THREE_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1,zone2);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertEquals(TWO_AGENTS + THREE_AGENTS,response.agents().demandés());
    }
  }

  @Nested
  class AdjustablePressureVentilationZoneMapping{

    @Test
    void givenAdjustableVentilationZone_whenMappingToResponse_thenReturnsCorrectType(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertInstanceOf(AdjustablePressureVentilationZoneResponse.class,response.zones().getFirst());
    }

    @Test
    void givenAdjustableVentilationZone_whenMappingToResponse_thenReturnsZoneId(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      AdjustablePressureVentilationZoneResponse response = getFirstAdjustableZone();

      assertEquals(ZONE_1_ID.toString(),response.getId());
    }

    @Test
    void givenAdjustableVentilationZone_whenMappingToResponse_thenReturnsFireState(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      AdjustablePressureVentilationZoneResponse response = getFirstAdjustableZone();

      assertEquals(UrgencyState.NORMAL.toString(),response.getFireState());
    }

    @Test
    void givenAdjustableVentilationZone_whenMappingToResponse_thenReturnsDistributionSpeed(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      AdjustablePressureVentilationZoneResponse response = getFirstAdjustableZone();

      assertEquals(ADJUSTABLE_DISTRIBUTION_SPEED,response.getVentilationDistributionSpeed());
    }

    @Test
    void givenAdjustableVentilationZone_whenMappingToResponse_thenReturnsReturnSpeed(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      AdjustablePressureVentilationZoneResponse response = getFirstAdjustableZone();

      assertEquals(ADJUSTABLE_RETURN_SPEED,response.getVentilationReturnSpeed());
    }

    private AdjustablePressureVentilationZoneResponse getFirstAdjustableZone(){
      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);
      return (AdjustablePressureVentilationZoneResponse) response.zones().getFirst();
    }
  }

  @Nested
  class SimpleVentilationZoneMapping{

    @Test
    void givenSimpleVentilationZone_whenMappingToResponse_thenReturnsCorrectType(){
      givenZoneWithSimpleVentilation(zone1,ZONE_1_ID,VENTILATION_OPEN);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertInstanceOf(SimpleVentilationZoneResponse.class,response.zones().getFirst());
    }

    @Test
    void givenSimpleVentilationZone_whenMappingToResponse_thenReturnsZoneId(){
      givenZoneWithSimpleVentilation(zone1,ZONE_1_ID,VENTILATION_OPEN);
      givenBuildingWithZones(zone1);

      SimpleVentilationZoneResponse response = getFirstSimpleZone();

      assertEquals(ZONE_1_ID.toString(),response.getId());
    }

    @Test
    void givenSimpleVentilationZone_whenMappingToResponse_thenReturnsFireState(){
      givenZoneWithSimpleVentilation(zone1,ZONE_1_ID,VENTILATION_OPEN);
      givenBuildingWithZones(zone1);

      SimpleVentilationZoneResponse response = getFirstSimpleZone();

      assertEquals(UrgencyState.NORMAL.toString(),response.getFireState());
    }

    @Test
    void givenSimpleVentilationOpen_whenMappingToResponse_thenReturnsOpenState(){
      givenZoneWithSimpleVentilation(zone1,ZONE_1_ID,VENTILATION_OPEN);
      givenBuildingWithZones(zone1);

      SimpleVentilationZoneResponse response = getFirstSimpleZone();

      assertEquals(VENTILATION_OPEN,response.getVentilation());
    }

    @Test
    void givenSimpleVentilationClosed_whenMappingToResponse_thenReturnsClosedState(){
      givenZoneWithSimpleVentilation(zone1,ZONE_1_ID,VENTILATION_CLOSED);
      givenBuildingWithZones(zone1);

      SimpleVentilationZoneResponse response = getFirstSimpleZone();

      assertEquals(VENTILATION_CLOSED,response.getVentilation());
    }

    private SimpleVentilationZoneResponse getFirstSimpleZone(){
      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);
      return (SimpleVentilationZoneResponse) response.zones().getFirst();
    }
  }

  @Nested
  class SmokePresenceMapping{

    @Test
    void givenZoneWithNoSmoke_whenMappingToResponse_thenSmokePresenceIsFalse(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenBuildingWithZones(zone1);

      ZoneResponse response = getFirstZone();

      assertFalse(response.getSmokePresence());
    }

    @Test
    void givenZoneWithSmoke_whenMappingToResponse_thenSmokePresenceIsTrue(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NON_NULL_SMOKE_CONCENTRATION);
      givenBuildingWithZones(zone1);

      ZoneResponse response = getFirstZone();

      assertTrue(response.getSmokePresence());
    }

    private ZoneResponse getFirstZone(){
      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);
      return response.zones().getFirst();
    }
  }

  @Nested
  class UrgencyStateMapping{

    @Test
    void givenZoneWithFireState_whenMappingToResponse_thenReturnsFireState(){
      givenZoneWithUrgencyState(zone1,UrgencyState.FIRE);
      givenBuildingWithZones(zone1);

      ZoneResponse response = getFirstZone();

      assertEquals(UrgencyState.FIRE.toString(),response.getFireState());
    }

    @Test
    void givenZoneWithNormalState_whenMappingToResponse_thenReturnsNormalState(){
      givenZoneWithUrgencyState(zone1,UrgencyState.NORMAL);
      givenBuildingWithZones(zone1);

      ZoneResponse response = getFirstZone();

      assertEquals(UrgencyState.NORMAL.toString(),response.getFireState());
    }

    private void givenZoneWithUrgencyState(HazardousMaterialZone zone, UrgencyState state) {
      when(zone.getId()).thenReturn(BuildingEmergencyStateMapperTest.ZONE_1_ID);
      when(zone.getUrgencyState()).thenReturn(state);
      when(zone.getNumberOfAgentsRequested()).thenReturn(NO_AGENTS);
      when(zone.getSmokeConcentration()).thenReturn(NO_SMOKE);
      when(zone.getDoors()).thenReturn(new ArrayList<>());
      when(zone.getVentilationSystem()).thenReturn(adjustableVentilation);
      when(adjustableVentilation.getCurrentState())
          .thenReturn(
              Map.of(
                  "distributionSpeed", String.valueOf(ADJUSTABLE_DISTRIBUTION_SPEED),
                  "returnSpeed", String.valueOf(ADJUSTABLE_RETURN_SPEED)));
    }

    private ZoneResponse getFirstZone(){
      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);
      return response.zones().getFirst();
    }
  }

  @Nested
  class DoorMapping{

    @Test
    void givenZoneWithDoor_whenMappingToResponse_thenReturnsDoorId(){
      givenZoneWithDoor(zone1,door1,DOOR_1_ID,IS_OPEN,!IS_LOCKED);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertEquals(DOOR_1_ID.toString(),response.portes().getFirst().id());
    }

    @Test
    void givenOpenDoor_whenMappingToResponse_thenIsClosedIsFalse(){
      givenZoneWithDoor(zone1,door1,DOOR_1_ID,IS_OPEN,!IS_LOCKED);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertFalse(response.portes().getFirst().fermée());
    }

    @Test
    void givenClosedDoor_whenMappingToResponse_thenIsClosedIsTrue(){
      givenZoneWithDoor(zone1,door1,DOOR_1_ID,!IS_OPEN,!IS_LOCKED);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertTrue(response.portes().getFirst().fermée());
    }

    @Test
    void givenLockedDoor_whenMappingToResponse_thenIsLockedIsTrue(){
      givenZoneWithDoor(zone1,door1,DOOR_1_ID,!IS_OPEN,IS_LOCKED);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertTrue(response.portes().getFirst().verrouillée());
    }

    @Test
    void givenUnlockedDoor_whenMappingToResponse_thenIsLockedIsFalse(){
      givenZoneWithDoor(zone1,door1,DOOR_1_ID,!IS_OPEN,!IS_LOCKED);
      givenBuildingWithZones(zone1);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertFalse(response.portes().getFirst().verrouillée());
    }

    @Test
    void givenMultipleZonesWithDoors_whenMappingToResponse_thenAggregatesAllDoors(){
      givenZoneWithDoor(zone1,door1,DOOR_1_ID,IS_OPEN,!IS_LOCKED);
      givenZoneWithDoor(zone2,door2,DOOR_2_ID,!IS_OPEN,IS_LOCKED);
      givenBuildingWithZones(zone1,zone2);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertEquals(TWO_DOORS,response.portes().size());
    }

    private void givenZoneWithDoor(HazardousMaterialZone zone,Door door,DoorId doorId,
        boolean isOpen,boolean isLocked){
      givenZoneWithAdjustableVentilation(zone,zone == zone1 ? ZONE_1_ID : ZONE_2_ID,NO_AGENTS,
          NO_SMOKE);
      when(zone.getDoors()).thenReturn(List.of(door));
      when(door.getId()).thenReturn(doorId);
      when(door.getIsOpen()).thenReturn(isOpen);
      when(door.getIsLocked()).thenReturn(isLocked);
    }
  }

  @Nested
  class MixedVentilationTypes{

    @Test
    void givenMixedVentilationZones_whenMappingToResponse_thenReturnsCorrectTypes(){
      givenZoneWithAdjustableVentilation(zone1,ZONE_1_ID,NO_AGENTS,NO_SMOKE);
      givenZoneWithSimpleVentilation(zone2,ZONE_2_ID,VENTILATION_OPEN);
      givenBuildingWithZones(zone1,zone2);

      BuildingEmergencyStateResponse response = BuildingEmergencyStateMapper.toResponse(building);

      assertEquals(TWO_ZONES,response.zones().size());
      assertInstanceOf(AdjustablePressureVentilationZoneResponse.class,response.zones().get(0));
      assertInstanceOf(SimpleVentilationZoneResponse.class,response.zones().get(1));
    }
  }

  private void givenEmptyBuilding() {
    when(building.getZones()).thenReturn(new ArrayList<>());
  }

  private void givenBuildingWithZones(HazardousMaterialZone... zones) {
    when(building.getZones()).thenReturn(List.of(zones));
  }

  private void givenZoneWithAdjustableVentilation(
      HazardousMaterialZone zone, ZoneId zoneId, int agents, int smokeConcentration) {
    when(zone.getId()).thenReturn(zoneId);
    when(zone.getUrgencyState()).thenReturn(UrgencyState.NORMAL);
    when(zone.getNumberOfAgentsRequested()).thenReturn(agents);
    when(zone.getSmokeConcentration()).thenReturn(smokeConcentration);
    when(zone.getDoors()).thenReturn(new ArrayList<>());
    when(zone.getVentilationSystem()).thenReturn(adjustableVentilation);
    when(zone.getNumberOfAgentsRequestedInZoneAndAllRooms()).thenReturn(agents);
    when(adjustableVentilation.getCurrentState())
        .thenReturn(
            Map.of(
                "distributionSpeed", String.valueOf(ADJUSTABLE_DISTRIBUTION_SPEED),
                "returnSpeed", String.valueOf(ADJUSTABLE_RETURN_SPEED)));
  }

  private void givenZoneWithSimpleVentilation(
      HazardousMaterialZone zone, ZoneId zoneId, String ventilationState) {
    when(zone.getId()).thenReturn(zoneId);
    when(zone.getUrgencyState()).thenReturn(UrgencyState.NORMAL);
    when(zone.getNumberOfAgentsRequested()).thenReturn(BuildingEmergencyStateMapperTest.NO_AGENTS);
    when(zone.getSmokeConcentration()).thenReturn(NO_SMOKE);
    when(zone.getDoors()).thenReturn(new ArrayList<>());
    when(zone.getVentilationSystem()).thenReturn(simpleVentilation);
    when(simpleVentilation.getCurrentState()).thenReturn(Map.of("isOpen", ventilationState));
  }
}
