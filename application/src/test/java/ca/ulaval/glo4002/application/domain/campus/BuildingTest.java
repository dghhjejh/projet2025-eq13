package ca.ulaval.glo4002.application.domain.campus;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessEvaluator;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.enums.FireFighterCallReason;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.buildings.PostalAddress;
import ca.ulaval.glo4002.application.domain.campus.buildings.VentilationSystemType;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.domain.event.SecurityEventName;
import ca.ulaval.glo4002.application.domain.event.parameters.EventParameter;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.gathering.GatheringId;
import ca.ulaval.glo4002.application.domain.gathering.GatheringType;
import ca.ulaval.glo4002.application.domain.gathering.HighRiskGathering;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BuildingTest{
  private static final ZoneId A_ZONE_ID = new ZoneId("PLT200");
  private static final ZoneId DIFFERENT_ZONE_ID = new ZoneId("PLT201");
  private static final BuildingId A_BUILDING_ID = new BuildingId("PLT");
  private static final RoomId A_ROOM_ID = new RoomId("room-001");
  private static final SecurityEventName FIRE_DECLARED_EVENT = SecurityEventName.FIRE;
  private static final SecurityEventName FIRE_EXTINGUISHED_EVENT = SecurityEventName.FIRE_ENDED;
  private static final SecurityEventName SMOKE_PRESENCE_EVENT = SecurityEventName.SMOKE_PRESENCE;
  private static final SecurityEventName PREALARM_EVENT = SecurityEventName.FIRE_PREALARM;
  private static final DoorId A_DOOR_ID = new DoorId("DOOR-001");
  private static final int SMOKE_CONCENTRATION_THRESHOLD_ONE = 20;
  private static final int SMOKE_CONCENTRATION_THRESHOLD_TWO = 50;
  private static final int SMOKE_CONCENTRATION_THRESHOLD_THREE = 80;
  private static final List<Action> EMPTY_ACTION_LIST = new ArrayList<>();
  private static final PostalAddress POSTAL_ADDRESS = new PostalAddress(
      "2500 Chemin Polytechnique, Montreal, QC H3T 1J4");
  private static final String BUILDING_NAME = "PLT";
  private static final InterventionId INTERVENTION_ID = new InterventionId();
  private static final String ID_ROOM = "room-001";
  private static final Idul AN_IDUL = new Idul("jdoe");
  private static final GatheringId GATHERING_ID = new GatheringId("academic-1");
  private static final int GATHERING_EXPECTED_ATTENDEES = 50;
  private static final GatheringType GATHERING_TYPE = GatheringType.ACADEMIC;
  private static final boolean IS_ON_FIRE = true;
  private static final boolean IS_NOT_ON_FIRE = false;

  private Building building;
  private Zone zone;
  private Zone otherZone;
  private Action action;
  private Action otherAction;
  private UserRepository userRepository;
  private ActionBuilder actionBuilder;
  private Action callFireFighterAction;
  private AccessCard accessCard;
  private AccessEvaluator accessEvaluator;

  @BeforeEach
  void setUp(){
    zone = mock(Zone.class);
    otherZone = mock(Zone.class);
    when(zone.getId()).thenReturn(A_ZONE_ID);
    when(otherZone.getId()).thenReturn(DIFFERENT_ZONE_ID);

    accessEvaluator = mock(AccessEvaluator.class);
    building = new Building(A_BUILDING_ID,BUILDING_NAME,POSTAL_ADDRESS,
        VentilationSystemType.CONTROLLABLE_SPEED,List.of(zone,otherZone),accessEvaluator);
    building.setId(A_BUILDING_ID);
    userRepository = mock(UserRepository.class);
    actionBuilder = new ActionBuilder();
    building.setPostalAddress(POSTAL_ADDRESS);
    callFireFighterAction = actionBuilder.createCallFireFighterAction(POSTAL_ADDRESS,
        FireFighterCallReason.FIRE);

    action = mock(Action.class);
    otherAction = mock(Action.class);
    accessCard = mock(AccessCard.class);
    when(accessCard.idul()).thenReturn(AN_IDUL);
  }

  private void setupZonesWithZone(Zone... zones){
    building.setZones(Arrays.asList(zones));
  }

  private List<EventParameter> createConcentrationParameters(int concentration){
    return List.of(new EventParameter("concentration",String.valueOf(concentration)));
  }

  private List<EventParameter> createEmptyParameters(){
    return new ArrayList<>();
  }

  private List<EventParameter> createInterventionIdParameters(){
    return List.of(new EventParameter("interventionId",BuildingTest.INTERVENTION_ID.toString()));
  }

  private List<EventParameter> createRoomIdParameter(){
    return List.of(new EventParameter("roomId",String.valueOf(A_ROOM_ID)));
  }

  private List<EventParameter> createRoomIdParameters(){
    return List.of(new EventParameter("roomId",BuildingTest.ID_ROOM));
  }

  private Gathering createGathering(){
    return new HighRiskGathering(BuildingTest.GATHERING_ID,
        BuildingTest.GATHERING_EXPECTED_ATTENDEES,BuildingTest.GATHERING_TYPE);
  }

  private List<EventParameter> gatheringStartParameters(String id,String type){
    return List.of(new EventParameter("gatheringId",id),
        new EventParameter("expectedAttendees",
            String.valueOf(BuildingTest.GATHERING_EXPECTED_ATTENDEES)),
        new EventParameter("gatheringType",type));
  }

  private List<EventParameter> gatheringEndParameters(String id){
    return List.of(new EventParameter("gatheringId",id));
  }

  @Nested
  class HandleEventRoutingTest{

    @Test
    void givenFireDeclaredEvent_whenHandleEvent_thenDelegatesToZoneTriggerFireEmergency() {
      when(zone.triggerFireEmergency(A_BUILDING_ID, actionBuilder, userRepository))
          .thenReturn(EMPTY_ACTION_LIST);

      building.handleEvent(
          A_ZONE_ID, FIRE_DECLARED_EVENT, createEmptyParameters(), actionBuilder, userRepository);

      verify(zone).triggerFireEmergency(A_BUILDING_ID, actionBuilder, userRepository);
    }

    @Test
    void givenFireExtinguishedEvent_whenHandleEvent_thenDelegatesToZoneResolveFireExtinguished() {
      when(zone.resolveFireExtinguished(A_BUILDING_ID, actionBuilder, userRepository))
          .thenReturn(EMPTY_ACTION_LIST);
      when(zone.isOnFire()).thenReturn(IS_NOT_ON_FIRE);

      building.handleEvent(
          A_ZONE_ID,
          FIRE_EXTINGUISHED_EVENT,
          createEmptyParameters(),
          actionBuilder,
          userRepository);

      verify(zone).resolveFireExtinguished(A_BUILDING_ID, actionBuilder, userRepository);
    }

    @Test
    void
        givenFirePrealarmEventWithNoLargeGroupInZone_whenHandleEvent_thenDelegatesToZoneTriggerFirePrealarm() {
      when(zone.triggerFirePrealarm(A_BUILDING_ID, actionBuilder)).thenReturn(EMPTY_ACTION_LIST);
      when(zone.hasLargeGroup()).thenReturn(IS_NOT_ON_FIRE);

      building.handleEvent(
          A_ZONE_ID, PREALARM_EVENT, createEmptyParameters(), actionBuilder, userRepository);

      verify(zone).triggerFirePrealarm(A_BUILDING_ID, actionBuilder);
    }

    @Test
    void
        givenFirePrealarmEventWithLargeGroupInZone_whenHandleEvent_thenDelegatesToZoneTriggerFireEmergency() {
      when(zone.triggerFireEmergency(A_BUILDING_ID, actionBuilder, userRepository))
          .thenReturn(EMPTY_ACTION_LIST);
      when(zone.hasLargeGroup()).thenReturn(IS_ON_FIRE);

      building.handleEvent(
          A_ZONE_ID, PREALARM_EVENT, createEmptyParameters(), actionBuilder, userRepository);

      verify(zone).triggerFireEmergency(A_BUILDING_ID, actionBuilder, userRepository);
    }

    @Test
    void givenSmokePresenceEvent_whenHandleEvent_thenDelegatesToZoneTriggerFirePrealarm() {
      when(zone.triggerFirePrealarm(A_BUILDING_ID, actionBuilder)).thenReturn(EMPTY_ACTION_LIST);

      building.handleEvent(
          A_ZONE_ID,
          SMOKE_PRESENCE_EVENT,
          createConcentrationParameters(SMOKE_CONCENTRATION_THRESHOLD_ONE),
          actionBuilder,
          userRepository);

      verify(zone).triggerFirePrealarm(A_BUILDING_ID, actionBuilder);
    }

    @Test
    void givenAgentDeployedToInterventionEvent_whenHandleEvent_thenDelegatesToZoneRegisterAgentDeploymentIn(){

      building.handleEvent(A_ZONE_ID,SecurityEventName.AGENT_DEPLOYED_TO_INTERVENTION,
          createInterventionIdParameters(),actionBuilder,userRepository);

      verify(zone).registerAgentDeployment(INTERVENTION_ID);
    }

    @Test
    void givenAgentLeftInterventionEvent_whenHandleEvent_thenDelegatesToZoneRegisterAgentDeploymentIn(){

      building.handleEvent(A_ZONE_ID,SecurityEventName.AGENT_LEFT_INTERVENTION,
          createInterventionIdParameters(),actionBuilder,userRepository);

      verify(zone).registerAgentLeftIntervention(INTERVENTION_ID);
    }

    @Test
    void givenRoomExitEvent_whenHandleEvent_thenDelegatesToZoneRegisterAgentDeploymentIn(){

      building.handleEvent(A_ZONE_ID,SecurityEventName.ROOM_EXIT,createRoomIdParameters(),
          actionBuilder,userRepository);

      verify(zone).registerRoomExit(A_ROOM_ID,A_BUILDING_ID,actionBuilder);
    }

    @Test
    void givenGatheringInZone_whenHandleEvent_thenDelegatesToZoneRegisterGatheringStarted(){
      List<EventParameter> params = gatheringStartParameters(String.valueOf(GATHERING_ID),
          String.valueOf(GATHERING_TYPE));
      Gathering gathering = createGathering();

      building.handleEvent(A_ZONE_ID,SecurityEventName.GATHERING_STARTED,params,actionBuilder,
          userRepository);

      verify(zone).registerGatheringStarted(gathering,A_BUILDING_ID,actionBuilder);
    }

    @Test
    void givenGatheringEnded_whenHandleEvent_thenDelegatesToZoneRegisterGatheringEnded(){
      List<EventParameter> parameters = gatheringEndParameters(String.valueOf(GATHERING_TYPE));

      building.handleEvent(A_ZONE_ID,SecurityEventName.GATHERING_ENDED,parameters,actionBuilder,
          userRepository);

      verify(zone).registerGatheringEnded(any(GatheringId.class),eq(A_BUILDING_ID),
          eq(actionBuilder));
    }
  }

  @Nested
  class TriggerFireEmergencyTest{

    @Test
    void whenTriggerFireEmergencyIn_thenHandlesDoorsInAllZones() {
      when(zone.triggerFireEmergency(A_BUILDING_ID, actionBuilder, userRepository))
          .thenReturn(EMPTY_ACTION_LIST);
      when(zone.applyRulesForDoorsDuringFire(actionBuilder, A_BUILDING_ID))
          .thenReturn(Collections.singletonList(action));
      when(otherZone.applyRulesForDoorsDuringFire(actionBuilder, A_BUILDING_ID))
          .thenReturn(Collections.singletonList(otherAction));

      building.triggerFireEmergencyIn(zone, actionBuilder, userRepository);

      verify(zone).applyRulesForDoorsDuringFire(actionBuilder, A_BUILDING_ID);
      verify(otherZone).applyRulesForDoorsDuringFire(actionBuilder, A_BUILDING_ID);
    }

    @Test
    void whenTriggerFireEmergencyIn_thenActivatesAllFireAlarmsInBuilding(){
      building.triggerFireEmergencyIn(zone,actionBuilder,userRepository);

      verify(zone).activateFireAlarms(A_BUILDING_ID,actionBuilder);
      verify(otherZone).activateFireAlarms(A_BUILDING_ID,actionBuilder);
    }

    @Test
    void whenTriggerFireEmergencyIn_thenAddsCallFirefightersAction(){
      List<Action> actions = building.triggerFireEmergencyIn(zone,actionBuilder,userRepository);

      assertTrue(actions.contains(callFireFighterAction));
    }

    @Test
    void givenZoneOnFire_whenTriggerFireEmergencyIn_thenDoesNotReturnCallFirefighterAction() {
      when(zone.isOnFire()).thenReturn(IS_ON_FIRE);

      List<Action> actions = building.triggerFireEmergencyIn(zone, actionBuilder, userRepository);

      assertFalse(actions.contains(callFireFighterAction));
    }

    @Test
    void givenZoneNotOnFire_whenTriggerFireEmergencyIn_thenReturnsCallFirefighterAction() {
      when(zone.isOnFire()).thenReturn(IS_NOT_ON_FIRE);

      List<Action> actions = building.triggerFireEmergencyIn(zone, actionBuilder, userRepository);

      assertTrue(actions.contains(callFireFighterAction));
    }

    @Test
    void whenTriggerFireEmergencyIn_thenOtherZoneTriggerFireEmergencyInOtherZones() {
      when(zone.triggerFireEmergency(A_BUILDING_ID, actionBuilder, userRepository))
          .thenReturn(EMPTY_ACTION_LIST);
      when(otherZone.triggerFireEmergencyInOtherZones(A_BUILDING_ID, actionBuilder))
          .thenReturn(EMPTY_ACTION_LIST);

      building.triggerFireEmergencyIn(zone, actionBuilder, userRepository);

      verify(otherZone).triggerFireEmergencyInOtherZones(A_BUILDING_ID, actionBuilder);
    }
  }

  @Nested
  class TriggerFireExtinguishedTest{
    @Test
    void givenNoActiveFiresInBuilding_whenTriggerFireExtinguishedIn_thenHandlesDoorsInAllDoors(){
      setupZonesWithZone(zone,otherZone);
      setupFireExtinguishedMocks();
      when(zone.applyRulesForDoorsWhenExtinguished(actionBuilder,A_BUILDING_ID))
          .thenReturn(Collections.singletonList(action));
      when(otherZone.applyRulesForDoorsWhenExtinguished(actionBuilder,A_BUILDING_ID))
          .thenReturn(Collections.singletonList(otherAction));

      building.triggerFireExtinguishedIn(zone,actionBuilder,userRepository);

      verify(zone).applyRulesForDoorsWhenExtinguished(actionBuilder,A_BUILDING_ID);
      verify(otherZone).applyRulesForDoorsWhenExtinguished(actionBuilder,A_BUILDING_ID);
    }

    private void setupFireExtinguishedMocks() {
      when(zone.resolveFireExtinguished(A_BUILDING_ID, actionBuilder, userRepository))
          .thenReturn(Collections.singletonList(action));
      when(zone.isOnFire()).thenReturn(false);
      when(otherZone.isOnFire()).thenReturn(false);
    }

    @Test
    void givenZoneExists_whenTriggerFireExtinguishedIn_thenDeactivatesAllFireAlarmsInBuilding(){
      building.triggerFireExtinguishedIn(zone,actionBuilder,userRepository);

      verify(zone).deactivateFireAlarms(A_BUILDING_ID,actionBuilder);
      verify(otherZone).deactivateFireAlarms(A_BUILDING_ID,actionBuilder);
    }

    @Test
    void
        givenOtherFiresInBuilding_whenTriggerFireExtinguishedIn_thenSpecifiedZoneTriggerFireEmergencyInOtherZones() {
      when(zone.isOnFire()).thenReturn(IS_ON_FIRE);
      when(otherZone.isOnFire()).thenReturn(IS_ON_FIRE);
      when(zone.triggerFireEmergencyInOtherZones(any(), any()))
          .thenReturn(List.of(mock(Action.class)));
      building.setZones(List.of(zone, otherZone));

      building.triggerFireExtinguishedIn(zone, actionBuilder, userRepository);

      verify(zone).triggerFireEmergencyInOtherZones(any(), any());
      verify(otherZone, never()).resolveFireExtinguished(any(), any(), any());
    }
  }

  @Nested
  class ProcessSmokeLevelsTest{

    @Test
    void givenConcentrationIsAtFirstThreshold_whenProcessSmokeLevelsIn_thenRedirectsToTriggerFirePrealarm(){
      building.processSmokeLevelsIn(zone,
          createConcentrationParameters(SMOKE_CONCENTRATION_THRESHOLD_ONE),actionBuilder,
          userRepository);

      verifyFirePrealarmTriggered();
    }

    private void verifyFirePrealarmTriggered(){
      verify(zone).triggerFirePrealarm(A_BUILDING_ID,actionBuilder);
      verify(zone,never()).triggerFireEmergency(A_BUILDING_ID,actionBuilder,userRepository);
    }

    @Test
    void givenConcentrationIsAtSecondThreshold_whenProcessSmokeLevelsIn_thenRedirectsToTriggerFire(){
      setupFireMocks();

      building.processSmokeLevelsIn(zone,
          createConcentrationParameters(SMOKE_CONCENTRATION_THRESHOLD_TWO),actionBuilder,
          userRepository);

      verifyFireTriggered();
    }

    private void setupFireMocks() {
      when(zone.triggerFireEmergency(A_BUILDING_ID, actionBuilder, userRepository))
          .thenReturn(new ArrayList<>());
    }

    private void verifyFireTriggered(){
      verify(zone).triggerFireEmergency(A_BUILDING_ID,actionBuilder,userRepository);
      verify(zone,never()).triggerFirePrealarm(A_BUILDING_ID,actionBuilder);
    }

    @Test
    void givenConcentrationIsAtThirdThreshold_whenProcessSmokeLevelsIn_thenRedirectsToTriggerFire(){
      setupFireMocks();

      building.processSmokeLevelsIn(zone,
          createConcentrationParameters(SMOKE_CONCENTRATION_THRESHOLD_THREE),actionBuilder,
          userRepository);

      verifyFireTriggered();
    }

    @Test
    void givenConcentrationParameter_whenProcessSmokeLevelsIn_thenShouldSetsConcentrationInZone(){
      building.processSmokeLevelsIn(zone,
          createConcentrationParameters(SMOKE_CONCENTRATION_THRESHOLD_ONE),actionBuilder,
          userRepository);

      verify(zone).setSmokeConcentrationLevel(SMOKE_CONCENTRATION_THRESHOLD_ONE);
    }
  }

  @Nested
  class RegisterAgentDeploymentTest{

    @Test
    void whenHandleEventWithAgentArrivedAtIntervention_thenShouldRegisterAgentsArrived(){
      building.handleEvent(zone.getId(),SecurityEventName.AGENT_ARRIVED_AT_INTERVENTION,
          createInterventionIdParameters(),actionBuilder,userRepository);

      verify(zone).registerAgentArrivedAtIntervention(INTERVENTION_ID,actionBuilder,A_BUILDING_ID);
    }
  }

  @Nested
  class ReportEntryWithoutCardTest{

    @Test
    void whenHandleEventWithNoCardEntry_thenShouldRegisterEntryWithoutCard(){
      building.handleEvent(zone.getId(),SecurityEventName.NO_CARD_ENTRY,createRoomIdParameter(),
          actionBuilder,userRepository);

      verify(zone).registerEntryWithoutCard(A_BUILDING_ID,A_ROOM_ID,actionBuilder);
    }
  }

  @Nested
  class RecordRoomExitTest{

    @BeforeEach
    void setUp(){
      setupZonesWithZone(zone);
    }

    @Test
    void givenRoomId_whenRecordRoomExitIn_thenShouldRegisterRoomExit(){
      building.recordRoomExitIn(zone,createRoomIdParameters(),actionBuilder);
      verify(zone).registerRoomExit(A_ROOM_ID,A_BUILDING_ID,actionBuilder);
    }

    @Test
    void givenZoneDoesNotExist_whenRecordRoomExitIn_thenReturnsEmptyList(){
      setupZonesWithZone(otherZone);
      List<Action> result = building.recordRoomExitIn(otherZone,createRoomIdParameters(),
          actionBuilder);
      assertTrue(result.isEmpty());
    }
  }

  @Nested
  class EvaluateAccessTest{
    private AccessContext accessContext;
    private static final boolean IS_SECURITY_AGENT = true;
    private static final boolean IS_NOT_SECURITY_AGENT = false;
    private static final boolean USER_DOES_NOT_EXIST = true;

    @BeforeEach
    void setUp(){
      setupZonesWithZone(zone);
      when(zone.evaluateAccessInCaseOfExtremeNegativePressure(any()))
          .thenReturn(AccessStatus.ALLOWED);
      accessContext = mock(AccessContext.class);
      when(accessContext.isSecurityAgent()).thenReturn(IS_SECURITY_AGENT);
      when(accessCard.idul()).thenReturn(AN_IDUL);
      when(accessCard.isSecurityAgent()).thenReturn(IS_SECURITY_AGENT);
    }

    @Test
    void givenAccessRequestForNonExistingZone_whenEvaluateAccess_thenShouldReturnDeniedAccessStatus(){
      AccessStatus actualAccessStatus = building.evaluateAccessTo(accessCard,DIFFERENT_ZONE_ID,
          A_DOOR_ID,A_ROOM_ID);

      assertEquals(AccessStatus.DENIED,actualAccessStatus);
    }

    @Test
    void givenAccessAllowed_whenEvaluateAccess_thenShouldRegisterRoomEntry() {
      when(accessEvaluator.evaluate(any(AccessContext.class))).thenReturn(AccessStatus.ALLOWED);

      building.evaluateAccessTo(accessCard, A_ZONE_ID, A_DOOR_ID, A_ROOM_ID);

      verify(zone).registerRoomEntry(eq(A_ROOM_ID), eq(accessCard));
    }

    @Test
    void givenAccessEvaluatorDeniesAccess_whenEvaluateAccess_thenShouldReturnDenied() {
      when(accessContext.isSecurityAgent()).thenReturn(IS_NOT_SECURITY_AGENT);
      when(accessEvaluator.evaluate(any(AccessContext.class))).thenReturn(AccessStatus.DENIED);

      AccessStatus status = building.evaluateAccessTo(accessCard, A_ZONE_ID, A_DOOR_ID, A_ROOM_ID);

      assertEquals(AccessStatus.DENIED, status);
    }

    @Test
    void
        givenACardIdWithNoCorrespondingUser_whenEvaluateAccess_thenShouldReturnDeniedAccessStatus() {
      when(accessCard.userDoesNotExist()).thenReturn(USER_DOES_NOT_EXIST);

      AccessStatus actualAccessStatus =
          building.evaluateAccessTo(accessCard, A_ZONE_ID, A_DOOR_ID, A_ROOM_ID);

      assertEquals(AccessStatus.DENIED, actualAccessStatus);
    }
  }

  @Nested
  class RegisterGatheringStartedTest{

    @Test
    void givenMultipleZones_whenRegisterGatheringStarted_thenTargetsCorrectZone(){
      List<EventParameter> parameters = gatheringStartParameters(String.valueOf(GATHERING_ID),
          String.valueOf(GATHERING_TYPE));
      Gathering gathering = createGathering();

      building.registerGatheringStarted(zone,parameters,actionBuilder);

      verify(zone).registerGatheringStarted(gathering,A_BUILDING_ID,actionBuilder);
      verify(otherZone,never()).registerGatheringStarted(any(),eq(A_BUILDING_ID),any());
    }
  }

  @Nested
  class RegisterGatheringEndedTest{

    private ActionBuilder actionBuilder;

    @BeforeEach
    void setup(){
      actionBuilder = new ActionBuilder();
    }

    @Test
    void givenActiveGathering_whenRegisterGatheringEnded_thenZoneHandlesEnd(){
      List<EventParameter> startParameters = gatheringStartParameters(String.valueOf(GATHERING_ID),
          String.valueOf(GATHERING_TYPE));
      building.registerGatheringStarted(zone,startParameters,actionBuilder);
      List<EventParameter> endParameters = gatheringEndParameters(String.valueOf(GATHERING_ID));

      building.registerGatheringEnded(zone,endParameters,actionBuilder);

      verify(zone).registerGatheringEnded(GATHERING_ID,A_BUILDING_ID,actionBuilder);
    }

    @Test
    void givenMissingGatheringId_whenRegisterGatheringEnded_thenThrowsException(){
      List<EventParameter> parameters = new ArrayList<>();

      assertThrows(IllegalArgumentException.class,
          () -> building.registerGatheringEnded(zone,parameters,actionBuilder));
    }
  }

  @Nested
  class GatheringParameterValidation{

    @Test
    void givenMissingGatheringId_whenRegisterGatheringStarted_thenThrowsException(){
      List<EventParameter> parameters = List.of(
          new EventParameter("nombrePersonnesPrevues",String.valueOf(GATHERING_EXPECTED_ATTENDEES)),
          new EventParameter("typeRassemblement",String.valueOf(GATHERING_TYPE)));

      assertThrows(IllegalArgumentException.class,
          () -> building.registerGatheringStarted(zone,parameters,actionBuilder));
    }

    @Test
    void givenMissingExpectedAttendees_whenRegisterGatheringStarted_thenThrowsException(){
      List<EventParameter> parameters = List.of(
          new EventParameter("identifiantRassemblement",String.valueOf(GATHERING_ID)),
          new EventParameter("typeRassemblement",String.valueOf(GATHERING_TYPE)));

      assertThrows(IllegalArgumentException.class,
          () -> building.registerGatheringStarted(zone,parameters,actionBuilder));
    }

    @Test
    void givenMissingGatheringType_whenRegisterGatheringStarted_thenThrowsException(){
      List<EventParameter> parameters = List.of(
          new EventParameter("identifiantRassemblement",String.valueOf(GATHERING_ID)),
          new EventParameter("nombrePersonnesPrevues",
              String.valueOf(GATHERING_EXPECTED_ATTENDEES)));

      assertThrows(IllegalArgumentException.class,
          () -> building.registerGatheringStarted(zone,parameters,actionBuilder));
    }
  }
}
