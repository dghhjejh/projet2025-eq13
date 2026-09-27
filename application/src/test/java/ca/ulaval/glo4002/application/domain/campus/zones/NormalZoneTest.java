package ca.ulaval.glo4002.application.domain.campus.zones;

import static ca.ulaval.glo4002.application.domain.actions.enums.ActionName.RequestAgent;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.SecurityRole;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.Status;
import ca.ulaval.glo4002.application.domain.bottin.User;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.Laboratory;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.campus.rooms.ServerRoom;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.gathering.GatheringId;
import ca.ulaval.glo4002.application.domain.gathering.GatheringType;
import ca.ulaval.glo4002.application.domain.gathering.HighRiskGathering;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationState;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class NormalZoneTest{
  private static final ZoneId ZONE_ID = new ZoneId("PLT200");
  private static final BuildingId BUILDING_ID = new BuildingId("PLT");
  private static final DoorId NON_EXISTENT_DOOR_ID = new DoorId("NON-EXISTENT-DOOR");
  private static final int REMAINING_NUMBER_OF_AGENTS_TO_DEPLOY = 3;
  private static final int MAXIMUM_NUMBER_OF_AGENTS_IN_ZONE = 4;
  private static final int NUMBER_OF_AGENT_TO_SEND_FOR_FIRE_PREALARM = 1;
  private static final int NUMBER_OF_AGENT_IN_ZONE_FOR_FIRE_PREALARM = 1;
  private static final int NO_AGENTS = 0;
  private static final InterventionId INTERVENTION_ID = new InterventionId();
  private static final InterventionId NOT_REQUESTED_INTERVENTION_ID = new InterventionId();
  private static final Idul AN_IDUL = new Idul("jdoe");
  private static final AgentId AGENT_ID = new AgentId();
  private static final Agent AGENT_ARRIVED = new Agent(AGENT_ID,INTERVENTION_ID,Priority.P1);
  private static final ZoneType TYPE = ZoneType.ADMINISTRATION;
  private static final int NUMBER_OF_AGENT_TO_SEND_FOR_LARGE_GROUP = 5;
  private static final int MINIMUM_NUMBER_OF_AGENTS_WHEN_RISKY_OR_SOCIAL_GATHERING = 5;
  private static final int REMAINING_AGENTS_TO_SEND_WITH_ONE_ARRIVED_AGENT = 4;
  private static final int ONE_AGENT = 1;
  private static final int SMOKE_CONCENTRATION = 0;
  private static final int EXPECTED_ATTENDEES = 60;
  private static final int SMALL_EXPECTED_ATTENDEES = 30;
  private static final AccessCard NULL_ACCESS_CARD = null;
  private static final DoorId NULL_DOOR_ID = null;
  private static final RoomId NULL_ROOM_ID = null;
  private static final Gathering LARGE_GROUP_GATHERING = new HighRiskGathering(
      new GatheringId("protest-1"),EXPECTED_ATTENDEES,GatheringType.PROTEST);
  private static final Gathering OTHER_RISKY_GATHERING = new HighRiskGathering(
      new GatheringId("protest-1"),SMALL_EXPECTED_ATTENDEES,GatheringType.OTHER_RISKY);
  private static final Gathering SOCIAL_GATHERING = new HighRiskGathering(
      new GatheringId("protest-1"),SMALL_EXPECTED_ATTENDEES,GatheringType.SOCIAL_ACTIVITY);
  private static final DoorId A_DOOR_ID = new DoorId("PLT200-BARRABLE-01");
  private static final boolean USER_IS_LAB_RESPONSIBLE_IN_BUILDING = true;
  private NormalZone zone;
  private Room room1;
  private Room room2;
  Door door;
  Runnable activateAlarm;
  private AccessCard defaultAccessCard;
  private UserRepository userRepository;
  private ActionBuilder actionBuilder;
  VentilationSystem ventilationSystem;

  @BeforeEach
  void initializeZone(){
    activateAlarm = mock(Runnable.class);
    userRepository = mock(UserRepository.class);
    room1 = mock(Room.class);
    room2 = mock(Room.class);
    door = mock(Door.class);
    defaultAccessCard = new AccessCard(AN_IDUL,SecurityRole.NONE);
    actionBuilder = new ActionBuilder(() -> INTERVENTION_ID,() -> AGENT_ID);
    ventilationSystem = mock(VentilationSystem.class);
    when(door.getId()).thenReturn(A_DOOR_ID);

    zone = new NormalZone(ZONE_ID,TYPE,false,List.of(door),List.of(room1,room2),UrgencyState.NORMAL,
        SMOKE_CONCENTRATION,false,ventilationSystem,new ArrayList<>(),new ArrayList<>(),
        new ArrayList<>(),new ArrayList<>());
  }

  @Nested
  class HandleFireTest{
    @Test
    void whenHandleFire_thenSetsUrgencyStateToFire(){
      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);

      assertEquals(UrgencyState.FIRE,zone.getUrgencyState());
    }

    @Test
    void givenNoAgentsDeployed_whenHandleFire_thenFourAgentRequestsAreSent(){
      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);

      assertEquals(MAXIMUM_NUMBER_OF_AGENTS_IN_ZONE,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenLargeGroupInZoneWithNoAgentsArrived_whenHandleFire_thenRightAmountAgentRequestsAreSent(){
      zone.addGathering(LARGE_GROUP_GATHERING);

      List<Action> actions = zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      long agentRequestCount = countAgentRequests(actions);

      assertEquals(NUMBER_OF_AGENT_TO_SEND_FOR_LARGE_GROUP,agentRequestCount);
    }

    @Test
    void givenLargeGroupWithOneAgentArrivedInZone_whenHandleFire_thenRightAmountAgentRequestsAreSent(){
      zone.addArrivedAgents(AGENT_ARRIVED);
      zone.addGathering(LARGE_GROUP_GATHERING);

      List<Action> actions = zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      long agentRequestCount = countAgentRequests(actions);

      assertEquals(REMAINING_AGENTS_TO_SEND_WITH_ONE_ARRIVED_AGENT,agentRequestCount);
    }

    @Test
    void givenLargeGroupWithOneAgentArrivedInRoom_whenHandleFire_thenRightAmountAgentRequestsAreSent(){
      zone.addGathering(LARGE_GROUP_GATHERING);
      when(room1.getAgentsArrivedCount()).thenReturn(ONE_AGENT);

      List<Action> actions = zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      long agentRequestCount = countAgentRequests(actions);

      assertEquals(REMAINING_AGENTS_TO_SEND_WITH_ONE_ARRIVED_AGENT,agentRequestCount);
    }

    @Test
    void givenOtherRiskyGatheringInZoneWithNoAgents_whenHandleFire_thenRightAmountAgentRequestsAreSent(){
      zone.addGathering(OTHER_RISKY_GATHERING);

      List<Action> actions = zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      long agentRequestCount = countAgentRequests(actions);

      assertEquals(MINIMUM_NUMBER_OF_AGENTS_WHEN_RISKY_OR_SOCIAL_GATHERING,agentRequestCount);
    }

    @Test
    void givenSocialGatheringInZoneWithNoAgents_whenHandleFire_thenRightAmountAgentRequestsAreSent(){
      zone.addGathering(SOCIAL_GATHERING);

      List<Action> actions = zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      long agentRequestCount = countAgentRequests(actions);

      assertEquals(MINIMUM_NUMBER_OF_AGENTS_WHEN_RISKY_OR_SOCIAL_GATHERING,agentRequestCount);
    }

    @Test
    void givenSocialGatheringWithOneAgentInZone_whenHandleFire_thenRightAmountAgentRequestsAreSent(){
      zone.addArrivedAgents(AGENT_ARRIVED);
      zone.addGathering(SOCIAL_GATHERING);

      List<Action> actions = zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      long agentRequestCount = countAgentRequests(actions);

      assertEquals(REMAINING_AGENTS_TO_SEND_WITH_ONE_ARRIVED_AGENT,agentRequestCount);
    }

    @Test
    void givenSocialGatheringWithOneAgentInRoom_whenHandleFire_thenRightAmountAgentRequestsAreSent(){
      zone.addGathering(SOCIAL_GATHERING);
      when(room1.getAgentsArrivedCount()).thenReturn(ONE_AGENT);

      List<Action> actions = zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      long agentRequestCount = countAgentRequests(actions);

      assertEquals(REMAINING_AGENTS_TO_SEND_WITH_ONE_ARRIVED_AGENT,agentRequestCount);
    }

    @Test
    void givenAnAgentAlreadyDeployed_whenHandleFire_thenSendsCorrectRemainingRequests(){
      zone.triggerFirePrealarm(BUILDING_ID,actionBuilder);
      dispatchAgents(NUMBER_OF_AGENT_IN_ZONE_FOR_FIRE_PREALARM);

      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);

      assertEquals(REMAINING_NUMBER_OF_AGENTS_TO_DEPLOY,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenMaximumNumberOfAgentsAlreadyDeployed_whenHandleFire_thenNoNewAgentsAreRequested(){
      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);
      dispatchAgents(MAXIMUM_NUMBER_OF_AGENTS_IN_ZONE);

      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);

      assertEquals(NO_AGENTS,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenNormalZone_whenHandleFire_thenCorrectMedianSpeedPressureAndOpen(){
      int expectedMedianSpeed = 75;
      int expectedPressureVariation = -25;
      SimpleVentilationState isOpen = SimpleVentilationState.CLOSED;

      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);

      verify(ventilationSystem).adjustVentilation(BUILDING_ID,ZONE_ID,expectedMedianSpeed,
          expectedPressureVariation,isOpen,actionBuilder);
    }

    @Test
    void whenHandleFireExtinguished_thenSetsUrgencyStateToNormal(){
      zone.resolveFireExtinguished(BUILDING_ID,actionBuilder,userRepository);

      assertEquals(UrgencyState.NORMAL,zone.getUrgencyState());
    }
  }

  @Nested
  class RegisterRoomEntryTest{
    private final Room room = mock(Room.class);
    private final Room secondRoom = mock(Room.class);
    private final RoomId A_ROOM_ID = new RoomId("LAB-101");
    private final RoomId SECOND_ROOM_ID = new RoomId("LAB-102");

    @BeforeEach
    void setUpRooms() {
      when(room.getId()).thenReturn(A_ROOM_ID);
      when(secondRoom.getId()).thenReturn(SECOND_ROOM_ID);
      zone.setRooms(List.of(room, secondRoom));
      zone.triggerFireEmergency(BUILDING_ID, actionBuilder, userRepository);
    }

    @Test
    void givenExistingRoomAndAccessCard_whenRegisterRoomEntry_thenIncrementsOccupationWithIdul(){
      zone.registerRoomEntry(A_ROOM_ID,defaultAccessCard);

      verify(room).incrementIdentifiedUserOccupation(AN_IDUL);
    }

    @Test
    void givenExistingRoomAndNullCardId_whenRegisterRoomEntry_thenIncrementsOccupation(){
      zone.registerRoomEntry(A_ROOM_ID,NULL_ACCESS_CARD);

      verify(room).incrementUnidentifiedUserOccupation();
    }
  }

  @Nested
  class HandleAgentDeployedTest{
    @Test
    void givenAnAgentRequested_whenHandleAgentDeployed_thenRemoveAgentRequest(){
      zone.triggerFirePrealarm(BUILDING_ID,actionBuilder);

      zone.registerAgentDeployment(INTERVENTION_ID);

      assertEquals(NUMBER_OF_AGENT_TO_SEND_FOR_FIRE_PREALARM - 1,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenAnAgentRequested_whenHandleAgentDeployed_thenSetCorrectNumberOfAgentsInZone(){
      zone.triggerFirePrealarm(BUILDING_ID,actionBuilder);

      zone.registerAgentDeployment(INTERVENTION_ID);

      assertEquals(NUMBER_OF_AGENT_IN_ZONE_FOR_FIRE_PREALARM,zone.getAgentsDeployedInZone().size());
    }

    @Test
    void givenAnAgentNotRequested_whenHandleAgentDeployed_thenTheNumberOfAgentsInZoneDoesNotChange(){
      zone.registerAgentDeployment(NOT_REQUESTED_INTERVENTION_ID);

      assertEquals(NO_AGENTS,zone.getAgentsDeployedInZone().size());
    }

    @Test
    void givenAnAgentNotRequested_whenHandleAgentDeployed_thenCallsHandleAgentDeployedForAllRooms(){
      zone.registerAgentDeployment(NOT_REQUESTED_INTERVENTION_ID);

      verify(room1).registerAgentDeployed(NOT_REQUESTED_INTERVENTION_ID);
      verify(room2).registerAgentDeployed(NOT_REQUESTED_INTERVENTION_ID);
    }
  }

  @Nested
  class HandleFireExtinguishedTest{
    @Test
    void whenHandleFireExtinguished_thenSetsUrgencyStateToNormal(){
      zone.resolveFireExtinguished(BUILDING_ID,actionBuilder,userRepository);

      assertEquals(UrgencyState.NORMAL,zone.getUrgencyState());
    }

    @Test
    void givenNormalZone_whenResolveVentilationFireExtinguished_thenCorrectMedianSpeedPressureAndOpen(){
      int expectedMedianSpeed = 50;
      int expectedPressureVariation = 10;
      SimpleVentilationState isOpen = SimpleVentilationState.OPEN;

      zone.resolveVentilationForFireExtinguished(BUILDING_ID,actionBuilder);

      verify(ventilationSystem).adjustVentilation(BUILDING_ID,ZONE_ID,expectedMedianSpeed,
          expectedPressureVariation,isOpen,actionBuilder);
    }
  }

  @Nested
  class HandleFireInRooms{
    private final ServerRoom serverRoom = mock(ServerRoom.class);
    private final Laboratory laboratoryRoom = mock(Laboratory.class);
    private final HashSet<Idul> notifiedResponsibles = new HashSet<>();

    @BeforeEach
    void setUpRooms(){
      Idul labResponsibleIdul = new Idul("LAB1");
      User labResponsible = mock(User.class);
      when(labResponsible.status()).thenReturn(Status.AVAILABLE);
      when(userRepository.findUserByIDUL(labResponsibleIdul)).thenReturn(labResponsible);

      zone.setRooms(List.of(serverRoom,laboratoryRoom));

      when(serverRoom.triggerFireEmergency(BUILDING_ID,ZONE_ID,actionBuilder,userRepository,
          notifiedResponsibles)).thenReturn(new ArrayList<>());
      when(laboratoryRoom.triggerFireEmergency(BUILDING_ID,ZONE_ID,actionBuilder,userRepository,
          notifiedResponsibles)).thenReturn(new ArrayList<>());
    }

    @Test
    void givenZoneWithRooms_whenHandleFire_thenCallsTriggerFireEmergencyOnAllRooms(){
      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);

      verify(serverRoom).triggerFireEmergency(BUILDING_ID,zone.getId(),actionBuilder,userRepository,
          notifiedResponsibles);
      verify(laboratoryRoom).triggerFireEmergency(BUILDING_ID,zone.getId(),actionBuilder,
          userRepository,notifiedResponsibles);
      assertEquals(UrgencyState.FIRE,zone.getUrgencyState());
    }

    @Test
    void givenZoneWithRooms_whenResolveFireExtinguished_thenCallsTriggerFireEmergencyExtinguishedOnAllRooms(){
      zone.resolveFireExtinguished(BUILDING_ID,actionBuilder,userRepository);

      verify(serverRoom).resolveFireExtinguished(BUILDING_ID,zone.getId(),actionBuilder,
          userRepository,notifiedResponsibles);
      verify(laboratoryRoom).resolveFireExtinguished(BUILDING_ID,zone.getId(),actionBuilder,
          userRepository,notifiedResponsibles);
      assertEquals(UrgencyState.NORMAL,zone.getUrgencyState());
    }
  }

  @Nested
  class HandleFirePrealarmTest{
    @Test
    void whenHandleFirePrealarm_thenSetsUrgencyStateToPrealarm(){
      zone.triggerFirePrealarm(BUILDING_ID,actionBuilder);

      assertEquals(UrgencyState.PREALARM,zone.getUrgencyState());
    }

    @Test
    void givenNoAgentsDeployed_whenHandleFirePrealarm_thenOneAgentIsRequested(){
      zone.triggerFirePrealarm(BUILDING_ID,actionBuilder);

      assertEquals(NUMBER_OF_AGENT_TO_SEND_FOR_FIRE_PREALARM,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenAgentsAlreadyDeployed_whenHandleFirePrealarm_thenNoAgentsAreRequested(){
      zone.triggerFirePrealarm(BUILDING_ID,actionBuilder);
      zone.registerAgentDeployment(INTERVENTION_ID);

      zone.triggerFirePrealarm(BUILDING_ID,actionBuilder);

      assertEquals(NO_AGENTS,zone.getNumberOfAgentsRequested());
    }

    @Test
    void givenNormalZone_whenHandleFirePrealarm_thenCorrectMedianSpeedPressureAndOpen(){
      int expectedMedianSpeed = 75;
      int expectedPressureVariation = -25;
      SimpleVentilationState isOpen = SimpleVentilationState.CLOSED;

      zone.triggerFireEmergency(BUILDING_ID,actionBuilder,userRepository);

      verify(ventilationSystem).adjustVentilation(BUILDING_ID,ZONE_ID,expectedMedianSpeed,
          expectedPressureVariation,isOpen,actionBuilder);
    }
  }

  private void dispatchAgents(int numberOfAgents){
    for (int i = 0; i < numberOfAgents; i++){
      zone.registerAgentDeployment(INTERVENTION_ID);
    }
  }

  private long countAgentRequests(List<Action> actions){
    return actions.stream().filter(Objects::nonNull)
        .filter(action -> action.getName().equals(RequestAgent)).count();
  }
}
