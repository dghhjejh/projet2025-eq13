package ca.ulaval.glo4002.application.domain.campus.rooms;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ServerRoomTest{

  private static final BuildingId BUILDING_ID = new BuildingId("1");
  private static final ZoneId ZONE_ID = new ZoneId("A");
  private static final RoomId ROOM_ID = new RoomId("1");
  private static final Idul LAB_SAFETY_RESPONSIBLE = new Idul("POUL");
  private final Set<Idul> notifiedResponsibles = new HashSet<>();
  private static final boolean IS_CLOSED = true;
  private static final boolean IS_OPEN = false;
  private ActionBuilder actionBuilder;
  private ServerRoom serverRoomWithElectric;
  private ServerRoom serverRoomWithoutElectric;
  private UserRepository userRepository;

  @BeforeEach
  void setup(){
    actionBuilder = new ActionBuilder();
    userRepository = mock(UserRepository.class);
    List<Agent> agentsDeployed = new ArrayList<>();
    List<Agent> agentsArrived = new ArrayList<>();
    List<RequestAgent> requestAgents = new ArrayList<>();

    serverRoomWithElectric = new ServerRoom(ROOM_ID,true,LAB_SAFETY_RESPONSIBLE,requestAgents,
        agentsDeployed,agentsArrived);
    serverRoomWithoutElectric = new ServerRoom(ROOM_ID,false,LAB_SAFETY_RESPONSIBLE,requestAgents,
        agentsDeployed,agentsArrived);
  }

  @Test
  void givenServerRoomWithElectricClosure_whenTriggerFire_Emergency_thenReturnsCloseElectricityAction(){
    List<Action> actions = serverRoomWithElectric.triggerFireEmergency(BUILDING_ID,ZONE_ID,
        actionBuilder,userRepository,notifiedResponsibles);

    Action expectedAction = actionBuilder.createOpenCloseElectricityAction(BUILDING_ID,ZONE_ID,
        ROOM_ID,IS_CLOSED);
    assertTrue(actions.contains(expectedAction));
  }

  @Test
  void givenServerRoomWithoutElectricClosure_whenTriggerFire_Emergency_thenReturnsNoActions(){
    List<Action> actions = serverRoomWithoutElectric.triggerFireEmergency(BUILDING_ID,ZONE_ID,
        actionBuilder,userRepository,notifiedResponsibles);

    assertTrue(actions.isEmpty());
  }

  @Test
  void givenServerRoomWithElectricClosure_whenTriggerFireEmergencyExtinguished_thenReturnsOpenElectricityAction(){
    List<Action> actions = serverRoomWithElectric.resolveFireExtinguished(BUILDING_ID,ZONE_ID,
        actionBuilder,userRepository,notifiedResponsibles);

    Action expectedAction = actionBuilder.createOpenCloseElectricityAction(BUILDING_ID,ZONE_ID,
        ROOM_ID,IS_OPEN);
    assertTrue(actions.contains(expectedAction));
  }

  @Test
  void givenServerRoomWithoutElectricClosure_whenTriggerFireEmergencyExtinguished_thenReturnsNoActions(){
    List<Action> actions = serverRoomWithoutElectric.resolveFireExtinguished(BUILDING_ID,ZONE_ID,
        actionBuilder,userRepository,notifiedResponsibles);

    assertTrue(actions.isEmpty());
  }
}
