package ca.ulaval.glo4002.application.domain.campus.zones;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.domain.access.SecurityRole;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.doors.FireDoor;
import ca.ulaval.glo4002.application.domain.campus.doors.Lockable;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.gathering.*;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class ZoneTest{
  private static final ZoneId ZONE_ID = new ZoneId("PLT200");
  private static final BuildingId BUILDING_ID = new BuildingId("PLT");
  private static final int NUMBER_OF_AGENTS_ARRIVED_IN_ZONE = 1;
  private static final int NUMBER_OF_AGENTS_TO_SEND_FOR_MEDIUM_RISK_GATHERING = 1;
  private static final int NUMBER_OF_AGENTS_TO_SEND_FOR_LOW_RISK_GATHERING = 1;
  private static final int NO_AGENTS = 0;
  private static final int ONE_AGENT = 1;
  private static final InterventionId INTERVENTION_ID = new InterventionId();
  private static final InterventionId NOT_REQUESTED_INTERVENTION_ID = new InterventionId();
  private static final InterventionId NOT_ARRIVED_INTERVENTION_ID = new InterventionId();
  private static final AgentId AGENT_ID = new AgentId();
  private static final Agent AGENT_DEPLOYED = new Agent(AGENT_ID,INTERVENTION_ID,Priority.P1);
  private static final Agent AGENT_ARRIVED = new Agent(AGENT_ID,INTERVENTION_ID,Priority.P1);
  private static final ZoneType TYPE = ZoneType.ADMINISTRATION;
  private static final int SMOKE_CONCENTRATION = 0;
  private static final String ID_1 = "idul1";
  private static final String ID_2 = "idul2";
  private static final Idul IDUL_1 = new Idul(ID_1);
  private static final Idul IDUL_2 = new Idul(ID_2);
  private static final int OCCUPATION_ROOM_1 = 5;
  private static final int OCCUPATION_ROOM_2 = 3;
  private static final int TOTAL_OCCUPATION = 8;
  private static final int TWO_ACCESSES = 2;
  private static final int ONE_ACCESS = 1;
  private static final int THREE_ACCESSES = 3;
  private static final Idul AN_IDUL = new Idul("jdoe");
  private static final AccessCard NULL_ACCESS_CARD = null;
  private static final AccessCard NOT_SECURITY_AGENT_ACCESS_CARD = new AccessCard(AN_IDUL,
      SecurityRole.NONE);
  private static final Idul AGENT_IDUL = new Idul("emart");
  private static final AccessCard FIRST_SECURITY_AGENT_ACCESS_CARD = new AccessCard(AN_IDUL,
      SecurityRole.SECURITY_AGENT);
  private static final AccessCard SECOND_SECURITY_AGENT_ACCESS_CARD = new AccessCard(AGENT_IDUL,
      SecurityRole.SECURITY_AGENT);
  private static final boolean IS_ZONE_ON_FIRE = true;
  private static final boolean CURRENTLY_HAS_ACTIVITY = true;
  private static final boolean HAS_ACTIVATE_ALARMS = true;
  private static final boolean SHOULD_ACTIVATE = true;

  private Zone zone;
  private Room room1;
  private Room room2;
  private UserRepository userRepository;
  private ActionBuilder actionBuilder;
  private VentilationSystem ventilationSystem;

  @BeforeEach
  void initializeZone(){
    userRepository = mock(UserRepository.class);
    room1 = mock(Room.class);
    room2 = mock(Room.class);
    actionBuilder = new ActionBuilder(() -> INTERVENTION_ID,() -> AGENT_ID);
    ventilationSystem = mock(VentilationSystem.class);
    zone = new TestZone(ZONE_ID,TYPE,!CURRENTLY_HAS_ACTIVITY,new ArrayList<>(),List.of(room1,room2),
        UrgencyState.NORMAL,SMOKE_CONCENTRATION,!HAS_ACTIVATE_ALARMS,ventilationSystem,
        new ArrayList<>(),new ArrayList<>(),new ArrayList<>(),new ArrayList<>());
  }

  private static class TestZone extends Zone{
    public TestZone(ZoneId zoneId,ZoneType type,boolean currentlyHasActivity,List<Door> doors,
        List<Room> rooms,UrgencyState urgencyState,int smokeConcentration,boolean hasActiveAlarms,
        VentilationSystem ventilationSystem,List<RequestAgent> agentsRequested,
        List<Agent> agentsDeployedInZone,List<Agent> agentsArrivedInZone,
        List<Gathering> gatheringsInZone) {
      super(zoneId, type, currentlyHasActivity, doors, rooms, urgencyState, smokeConcentration,
          hasActiveAlarms, ventilationSystem, agentsRequested, agentsDeployedInZone,
          agentsArrivedInZone, gatheringsInZone);
    }

    @Override
    public List<Action> triggerFireEmergency(BuildingId buildingId,ActionBuilder actionBuilder,
        UserRepository userRepository){
      return new ArrayList<>();
    }

    @Override
    public List<Action> triggerFireEmergencyInOtherZones(BuildingId buildingId,
        ActionBuilder actionBuilder){
      return new ArrayList<>();
    }

    @Override
    public List<Action> triggerFirePrealarm(BuildingId buildingId,ActionBuilder actionBuilder){
      return new ArrayList<>();
    }

    @Override
    public List<Action> resolveVentilationForFireExtinguished(BuildingId buildingId,
        ActionBuilder actionBuilder){
      return new ArrayList<>();
    }
  }

  @Nested
  class ResolveFireExtinguishedTest{
    @Test
    void whenResolveFireExtinguished_thenSetsUrgencyStateToNormal(){
      zone.resolveFireExtinguished(BUILDING_ID,actionBuilder,userRepository);

      assertEquals(UrgencyState.NORMAL,zone.getUrgencyState());
    }
  }

  @Nested
  class RegisterAgentArrivalTest{
    @Test
    void whenRegisterAgentArrivedAndAgentNotDeployedInZone_thenCallsRegisterAgentArrivedForAllRooms(){
      ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);
      zone.registerAgentArrivedAtIntervention(INTERVENTION_ID,actionBuilder,BUILDING_ID);

      verify(room1).registerAgentArrived(eq(INTERVENTION_ID),eq(actionBuilder),eq(BUILDING_ID),
          eq(ZONE_ID),eq(!IS_ZONE_ON_FIRE),captor.capture());
      verify(room1).registerAgentArrived(eq(INTERVENTION_ID),eq(actionBuilder),eq(BUILDING_ID),
          eq(ZONE_ID),eq(!IS_ZONE_ON_FIRE),captor.capture());
    }

    @Test
    void whenRegisterAgentArrivedAndAgentWasDeployedInZone_thenReturnsNoActions(){
      zone.addDeployedAgents(AGENT_DEPLOYED);

      List<Action> actions = zone.registerAgentArrivedAtIntervention(INTERVENTION_ID,actionBuilder,
          BUILDING_ID);

      assertTrue(actions.isEmpty());
    }

    @Test
    void whenRegisterAgentArrivedAndAgentWasDeployedInZone_thenAddAgentToArrivedList(){
      zone.addDeployedAgents(AGENT_DEPLOYED);

      zone.registerAgentArrivedAtIntervention(INTERVENTION_ID,actionBuilder,BUILDING_ID);

      assertEquals(NUMBER_OF_AGENTS_ARRIVED_IN_ZONE,zone.getNumberOfAgentsArrived());
    }

    @Test
    void whenRegisterAgentArrivedAndAgentWasDeployedInZone_thenRemovesAgentFromDeployedList(){
      zone.addDeployedAgents(AGENT_DEPLOYED);

      zone.registerAgentArrivedAtIntervention(INTERVENTION_ID,actionBuilder,BUILDING_ID);

      assertEquals(NO_AGENTS,zone.getNumberOfAgentsDeployed());
    }

    @Test
    void whenRegisterAgentArrivedAndAgentWasRequestedInZone_thenAddAgentToArrivedList(){
      zone.addArrivedAgents(AGENT_DEPLOYED);

      zone.registerAgentArrivedAtIntervention(INTERVENTION_ID,actionBuilder,BUILDING_ID);

      assertEquals(NUMBER_OF_AGENTS_ARRIVED_IN_ZONE,zone.getNumberOfAgentsArrived());
    }

    @Test
    void whenRegisterAgentArrivedAndAgentWasRequestedInZone_thenRemovesAgentFromDeployedList(){
      zone.addArrivedAgents(AGENT_DEPLOYED);

      zone.registerAgentArrivedAtIntervention(INTERVENTION_ID,actionBuilder,BUILDING_ID);

      assertEquals(NO_AGENTS,zone.getNumberOfAgentsDeployed());
    }

    @Test
    void whenRegisterAgentArrivedAndInterventionIdDoesNotMatchDeployedAgent_thenCallsRegisterAgentForAllRooms(){
      zone.addDeployedAgents(AGENT_DEPLOYED);
      ArgumentCaptor<Runnable> captor = ArgumentCaptor.forClass(Runnable.class);

      zone.registerAgentArrivedAtIntervention(NOT_REQUESTED_INTERVENTION_ID,actionBuilder,
          BUILDING_ID);

      verify(room1).registerAgentArrived(eq(NOT_REQUESTED_INTERVENTION_ID),eq(actionBuilder),
          eq(BUILDING_ID),eq(ZONE_ID),eq(!IS_ZONE_ON_FIRE),captor.capture());
      verify(room1).registerAgentArrived(eq(NOT_REQUESTED_INTERVENTION_ID),eq(actionBuilder),
          eq(BUILDING_ID),eq(ZONE_ID),eq(!IS_ZONE_ON_FIRE),captor.capture());
    }
  }

  @Nested
  class RegisterAgentLeavingTest{
    @Test
    void givenAgentArrivedInZone_whenRegisterAgentLeft_thenRemovesAgentFromZone(){
      zone.addArrivedAgents(AGENT_ARRIVED);

      zone.registerAgentLeftIntervention(INTERVENTION_ID);

      assertEquals(NO_AGENTS,zone.getNumberOfAgentsArrived());
    }

    @Test
    void givenUnmatchedAgentArrivedInZone_whenRegisterAgentLeft_thenNoAgentsRemoved(){
      zone.addArrivedAgents(AGENT_ARRIVED);
      int numberOfAgentsArrivedBeforeLeaving = zone.getNumberOfAgentsArrived();

      zone.registerAgentLeftIntervention(NOT_ARRIVED_INTERVENTION_ID);

      int numberOfAgentsArrivedAfterLeaving = zone.getNumberOfAgentsArrived();
      assertEquals(numberOfAgentsArrivedBeforeLeaving,numberOfAgentsArrivedAfterLeaving);
    }
  }

  @Nested
  class ActivateFireAlarmsTest{
    @Test
    void whenActivatingFireAlarms_thenReturnsActivateFireAlarmActionInZone(){
      List<Action> actions = zone.activateFireAlarms(BUILDING_ID,actionBuilder);

      Action expectedAction = actionBuilder.createActivateFireAlarmAction(BUILDING_ID,ZONE_ID,
          SHOULD_ACTIVATE);
      assertTrue(containsActionOfType(expectedAction.getName(),actions));
    }

    @Test
    void whenActivatingFireAlarms_thenAlarmIsRingingInZone(){
      zone.activateFireAlarms(BUILDING_ID,actionBuilder);

      assertTrue(zone.areAlarmsActive());
    }

    @Test
    void whenActivatingFireAlarms_thenActivateFireAlarmActionIsCreated(){
      List<Action> actions = zone.activateFireAlarms(BUILDING_ID,actionBuilder);

      assertTrue(containsActionOfType(ActionName.ActivateFireAlarm,actions));
    }

    @Test
    void whenDeactivatingFireAlarms_thenReturnsActivateFireAlarmActionInZone(){
      zone.activateFireAlarms(BUILDING_ID,actionBuilder);

      List<Action> actions = zone.deactivateFireAlarms(BUILDING_ID,actionBuilder);

      Action expectedAction = actionBuilder.createActivateFireAlarmAction(BUILDING_ID,ZONE_ID,
          !SHOULD_ACTIVATE);
      assertTrue(containsActionOfType(expectedAction.getName(),actions));
    }

    @Test
    void whenDeactivatingFireAlarms_thenAlarmIsNotRingingInZone(){
      zone.deactivateFireAlarms(BUILDING_ID,actionBuilder);

      assertFalse(zone.areAlarmsActive());
    }

    @Test
    void whenDeactivatingFireAlarms_thenActivateFireAlarmActionIsCreated(){
      zone.activateFireAlarms(BUILDING_ID,actionBuilder);

      List<Action> actions = zone.deactivateFireAlarms(BUILDING_ID,actionBuilder);

      assertTrue(containsActionOfType(ActionName.ActivateFireAlarm,actions));
    }
  }

  @Nested
  class ApplyRulesForDoorsDuringFireTest{
    private final FireDoor fireDoor = mock(FireDoor.class);
    private final Lockable lockableDoor = mock(Lockable.class);

    @BeforeEach
    void setUpDoors(){
      zone.setDoors(List.of(fireDoor,lockableDoor));
    }

    @Test
    void whenApplyRulesForDoorsDuringFire_thenCallsTriggerFireEmergencyOnAllDoors(){
      zone.applyRulesForDoorsDuringFire(actionBuilder,BUILDING_ID);

      verify(fireDoor).triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);
      verify(lockableDoor).triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);
    }

    @Test
    void whenApplyRulesForDoorsWhenExtinguished_thenCallsTriggerFireEmergencyExtinguishedOnAllDoors(){
      zone.applyRulesForDoorsWhenExtinguished(actionBuilder,BUILDING_ID);

      verify(fireDoor).resolveFireExtinguished(actionBuilder,BUILDING_ID,ZONE_ID);
      verify(lockableDoor).resolveFireExtinguished(actionBuilder,BUILDING_ID,ZONE_ID);
    }
  }

  @Nested
  class RegisterRoomExitTest{
    private final Room room = mock(Room.class);
    private final RoomId A_ROOM_ID = new RoomId("LAB-101");

    @BeforeEach
    void setUpRooms() {
      when(room.getId()).thenReturn(A_ROOM_ID);
      zone.setRooms(List.of(room));
    }

    @Test
    void givenExistingRoom_whenRegisterRoomExit_thenDecrementsOccupation(){
      zone.registerRoomExit(A_ROOM_ID,BUILDING_ID,actionBuilder);

      verify(room).decrementOccupation();
    }
  }

  @Nested
  class EvaluateAccessInCaseOfExtremeNegativePressureTest{
    @BeforeEach
    void setupVentilationSystem() {
      when(ventilationSystem.hasExtremeNegativePressure()).thenReturn(true);
    }

    @Test
    void givenExtremeNegativePressureAndRequestAccessWithoutCard_whenEvaluatingAccess_thenDeniesAccess(){
      AccessStatus accessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(NULL_ACCESS_CARD);

      assertEquals(AccessStatus.DENIED,accessStatus);
    }

    @Test
    void givenExtremeNegativePressureAndRequestAccessWithoutBeingSecurityAgent_whenEvaluatingAccess_thenDeniesAccess(){
      AccessStatus accessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(NOT_SECURITY_AGENT_ACCESS_CARD);

      assertEquals(AccessStatus.DENIED,accessStatus);
    }

    @Test
    void givenExtremeNegativePressureAndSameSecurityAgentRequestsAccessLessThanThreeTimesConsecutively_whenEvaluatingAccess_thenDeniesAccess(){
      AccessStatus firstAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      AccessStatus secondAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);

      assertEquals(AccessStatus.DENIED,firstAccessStatus);
      assertEquals(AccessStatus.DENIED,secondAccessStatus);
    }

    @Test
    void givenExtremeNegativePressureAndSameSecurityAgentRequestsAccessThreeTimesConsecutively_whenEvaluatingAccess_thenAllowsAccessTheThirdTime(){
      AccessStatus firstAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      AccessStatus secondAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      AccessStatus thirdAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);

      assertEquals(AccessStatus.DENIED,firstAccessStatus);
      assertEquals(AccessStatus.DENIED,secondAccessStatus);
      assertEquals(AccessStatus.ALLOWED,thirdAccessStatus);
    }

    @Test
    void givenExtremeNegativePressureWithMultipleAccessRequestsAndNotASameSecurityAgentRequestingAccessThreeTimesConsecutively_whenEvaluatingAccess_thenDeniesAccess(){
      AccessStatus firstAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      AccessStatus secondAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(SECOND_SECURITY_AGENT_ACCESS_CARD);
      AccessStatus thirdAccessStatus = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);

      assertEquals(AccessStatus.DENIED,firstAccessStatus);
      assertEquals(AccessStatus.DENIED,secondAccessStatus);
      assertEquals(AccessStatus.DENIED,thirdAccessStatus);
    }

    @Test
    void givenThreeConsecutiveAttemptsGrantAccessAndTheSameAgentRequestsFourthTimeConsecutively_whenEvaluatingAccess_thenAccessIsDeniedTheFourthTime(){
      zone.evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      zone.evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      zone.evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);

      AccessStatus fourthAttempt = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);

      assertEquals(AccessStatus.DENIED,fourthAttempt);
    }

    @Test
    void givenExtremeNegativePressureThenPressureNormalizesAndBecomesExtremeAgain_whenEvaluatingAccess_thenCounterIsResetAndAccessIsDenied(){
      boolean hasExtremeNegativePressure = false;
      boolean hasNormalPressure = true;
      zone.evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      zone.evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      when(ventilationSystem.hasExtremeNegativePressure()).thenReturn(hasExtremeNegativePressure);
      zone.evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);
      when(ventilationSystem.hasExtremeNegativePressure()).thenReturn(hasNormalPressure);

      AccessStatus status = zone
          .evaluateAccessInCaseOfExtremeNegativePressure(FIRST_SECURITY_AGENT_ACCESS_CARD);

      assertEquals(AccessStatus.DENIED,status);
    }
  }

  @Nested
  class RegisterGatheringStartedTest{
    public static final int NUMBER_OF_AGENTS_TO_SEND_FOR_HIGH_RISK_GATHERING = 2;
    public static final int AN_EXPECTED_ATTENDEES_AMOUNT = 30;
    public static final int A_LARGE_GROUP_OF_EXPECTED_ATTENDEES = 50;
    public static final int A_SMALL_GROUP_OF_EXPECTED_ATTENDEES = 30;
    private Gathering highRiskGathering;
    private Gathering mediumRiskGathering;
    private Gathering lowRiskGatheringLargeGroup;
    private Gathering lowRiskGatheringSmallGroup;

    @BeforeEach
    void setupGatherings(){
      highRiskGathering = new HighRiskGathering(new GatheringId("protest-1"),
          AN_EXPECTED_ATTENDEES_AMOUNT,GatheringType.PROTEST);
      mediumRiskGathering = new MediumRiskGathering(new GatheringId("risk-1"),
          AN_EXPECTED_ATTENDEES_AMOUNT,GatheringType.OTHER_RISKY);
      lowRiskGatheringLargeGroup = new LowRiskGathering(new GatheringId("academic-1"),
          A_LARGE_GROUP_OF_EXPECTED_ATTENDEES,GatheringType.ACADEMIC);
      lowRiskGatheringSmallGroup = new LowRiskGathering(new GatheringId("social-1"),
          A_SMALL_GROUP_OF_EXPECTED_ATTENDEES,GatheringType.SOCIAL_ACTIVITY);
    }

    @Test
    void givenHighRiskGathering_whenRegisterGatheringStarted_thenSendsTwoAgents(){
      List<Action> actions = zone.registerGatheringStarted(highRiskGathering,BUILDING_ID,
          actionBuilder);

      Action expectedAction = new RequestAgent(AGENT_ID,ZONE_ID,Priority.P2,INTERVENTION_ID);
      assertTrue(specificNumberOfActionsAreEqual(expectedAction,actions,
          NUMBER_OF_AGENTS_TO_SEND_FOR_HIGH_RISK_GATHERING));
      assertEquals(NUMBER_OF_AGENTS_TO_SEND_FOR_HIGH_RISK_GATHERING,
          zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenLowRiskGatheringWithLargeGroup_whenRegisterGatheringStarted_thenSendsSecurityAgentsP2(){
      List<Action> actions = zone.registerGatheringStarted(highRiskGathering,BUILDING_ID,
          actionBuilder);

      Action expectedAction = new RequestAgent(AGENT_ID,ZONE_ID,Priority.P2,INTERVENTION_ID);
      assertTrue(specificNumberOfActionsAreEqual(expectedAction,actions,
          NUMBER_OF_AGENTS_TO_SEND_FOR_HIGH_RISK_GATHERING));
    }

    @Test
    void givenHighRiskGathering_whenRegisterGatheringStarted_thenDelegatesApplyGatheringStartedToDoors(){
      Lockable door1 = mock(Lockable.class);
      Lockable door2 = mock(Lockable.class);
      when(door1.applyGatheringStarted(any(),any(),any())).thenReturn(List.of(mock(Action.class)));
      when(door2.applyGatheringStarted(any(),any(),any())).thenReturn(List.of(mock(Action.class)));
      zone.setDoors(List.of(door1,door2));

      zone.registerGatheringStarted(highRiskGathering,BUILDING_ID,actionBuilder);

      verify(door1).applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);
      verify(door2).applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);
    }

    @Test
    void givenMediumRiskGathering_whenRegisterGatheringStarted_thenSendsOneAgent(){
      List<Action> actions = zone.registerGatheringStarted(mediumRiskGathering,BUILDING_ID,
          actionBuilder);

      assertTrue(containsActionOfType(ActionName.RequestAgent,actions));
      assertEquals(ONE_AGENT,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenMediumRiskGathering_whenRegisterGatheringStarted_thenSendsSecurityAgentP2(){
      List<Action> actions = zone.registerGatheringStarted(mediumRiskGathering,BUILDING_ID,
          actionBuilder);

      Action expectedAction = new RequestAgent(AGENT_ID,ZONE_ID,Priority.P2,INTERVENTION_ID);
      assertTrue(specificNumberOfActionsAreEqual(expectedAction,actions,
          NUMBER_OF_AGENTS_TO_SEND_FOR_MEDIUM_RISK_GATHERING));
    }

    @Test
    void givenMediumRiskGathering_whenRegisterGatheringStarted_thenDoesNotDelegateApplyGatheringStartedToDoors(){
      Lockable door = mock(Lockable.class);
      zone.setDoors(List.of(door));

      zone.registerGatheringStarted(mediumRiskGathering,BUILDING_ID,actionBuilder);

      verify(door,never()).applyGatheringStarted(any(),any(),any());
    }

    @Test
    void givenLowRiskGatheringWithLargeGroup_whenRegisterGatheringStarted_thenSendsOneAgent(){
      List<Action> actions = zone.registerGatheringStarted(lowRiskGatheringLargeGroup,BUILDING_ID,
          actionBuilder);

      assertTrue(containsActionOfType(ActionName.RequestAgent,actions));
      assertEquals(1,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenLowRiskGatheringWithLargeGroup_whenRegisterGatheringStarted_thenSendsSecurityAgentP3(){
      List<Action> actions = zone.registerGatheringStarted(lowRiskGatheringLargeGroup,BUILDING_ID,
          actionBuilder);

      Action expectedAction = new RequestAgent(AGENT_ID,ZONE_ID,Priority.P3,INTERVENTION_ID);
      assertTrue(specificNumberOfActionsAreEqual(expectedAction,actions,
          NUMBER_OF_AGENTS_TO_SEND_FOR_LOW_RISK_GATHERING));
    }

    @Test
    void givenLowRiskGatheringWithPreexistingLowRiskGatheringTotalingLargeGroup_whenRegisterGatheringStarted_thenSendsOneAgent(){
      zone.registerGatheringStarted(lowRiskGatheringSmallGroup,BUILDING_ID,actionBuilder);

      List<Action> actions = zone.registerGatheringStarted(lowRiskGatheringSmallGroup,BUILDING_ID,
          actionBuilder);

      assertTrue(containsActionOfType(ActionName.RequestAgent,actions));
      assertEquals(1,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenLowRiskGatheringWithLargeGroup_whenRegisterGatheringStarted_thenDoesNotDelegateApplyGatheringToDoors(){
      Lockable door = mock(Lockable.class);
      zone.setDoors(List.of(door));

      zone.registerGatheringStarted(lowRiskGatheringLargeGroup,BUILDING_ID,actionBuilder);

      verify(door,never()).applyGatheringStarted(any(),any(),any());
    }

    @Test
    void givenLowRiskGatheringWithSmallGroup_whenRegisterGatheringStarted_thenSendsNoAgents(){
      List<Action> actions = zone.registerGatheringStarted(lowRiskGatheringSmallGroup,BUILDING_ID,
          actionBuilder);

      assertTrue(actions.isEmpty());
    }
  }

  @Nested
  class RegisterGatheringEndedTest{
    public static final int EXPECTED_ATTENDEES = 100;
    private Gathering highRiskGathering;
    private static final GatheringId GATHERING_ID = new GatheringId("gathering-1");

    @BeforeEach
    void setupGatherings(){
      highRiskGathering = new HighRiskGathering(GATHERING_ID,EXPECTED_ATTENDEES,
          GatheringType.PROTEST);
    }

    @Test
    void givenHighRiskGathering_whenRegisterGatheringEnded_thenDelegatesHandlingOfGatheringEndedToDoors(){
      zone.registerGatheringStarted(highRiskGathering,BUILDING_ID,actionBuilder);

      Lockable door1 = mock(Lockable.class);
      Lockable door2 = mock(Lockable.class);
      when(door1.applyGatheringEnded(any(),any(),any())).thenReturn(List.of(mock(Action.class)));
      when(door2.applyGatheringEnded(any(),any(),any())).thenReturn(List.of(mock(Action.class)));
      zone.setDoors(List.of(door1,door2));

      zone.registerGatheringEnded(GATHERING_ID,BUILDING_ID,actionBuilder);

      verify(door1).applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);
      verify(door2).applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);
    }
  }

  @Nested
  class OccupationTest{

    @BeforeEach
    void setUpOccupation(){
      zone.setRooms(List.of(room1,room2));
    }

    @Test
    void whenZoneHasRoomsWithOccupation_thenReturnsSumOfOccupations() {
      when(room1.getCurrentOccupation()).thenReturn(OCCUPATION_ROOM_1);
      when(room2.getCurrentOccupation()).thenReturn(OCCUPATION_ROOM_2);

      int totalOccupation = zone.getTotalOccupation();

      assertEquals(TOTAL_OCCUPATION, totalOccupation);
    }

    @Test
    void whenZoneContainsRoomsWithIdentifiedPeople_thenReturnsUniqueOccupantsFromAllRooms(){
      Map<Idul, Integer> room1Accesses = Map.of(IDUL_1,TWO_ACCESSES);
      Map<Idul, Integer> room2Accesses = Map.of(IDUL_2,ONE_ACCESS,IDUL_1,ONE_ACCESS);
      when(room1.getConsecutiveAccessesByPerson()).thenReturn(room1Accesses);
      when(room2.getConsecutiveAccessesByPerson()).thenReturn(room2Accesses);

      List<String> occupants = zone.getIdentifiedOccupants();

      assertEquals(Set.of(ID_1,ID_2),new HashSet<>(occupants));
    }

    @Test
    void whenZoneContainsRoomsWithConsecutiveAccesses_thenReturnsAccessCountsPerPerson(){
      Map<Idul, Integer> room1Accesses = Map.of(IDUL_1,TWO_ACCESSES);
      Map<Idul, Integer> room2Accesses = Map.of(IDUL_1,THREE_ACCESSES,IDUL_2,THREE_ACCESSES);
      when(room1.getConsecutiveAccessesByPerson()).thenReturn(room1Accesses);
      when(room2.getConsecutiveAccessesByPerson()).thenReturn(room2Accesses);

      Map<String, Integer> result = zone.getConsecutiveAccessesByPerson();

      assertEquals(OCCUPATION_ROOM_1,result.get(ID_1));
      assertEquals(OCCUPATION_ROOM_2,result.get(ID_2));
    }
  }

  private boolean containsActionOfType(ActionName actionName,List<Action> actions){
    return actions.stream().anyMatch(action -> action.getName().equals(actionName));
  }

  private boolean specificNumberOfActionsAreEqual(Action expectedAction,List<Action> actions,
      int numberOfActionsToMatch){
    return numberOfActionsToMatch == Collections.frequency(actions,expectedAction);
  }
}
