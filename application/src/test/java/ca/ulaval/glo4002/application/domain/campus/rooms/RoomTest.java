package ca.ulaval.glo4002.application.domain.campus.rooms;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class RoomTest{
  private static final Priority PRIORITY = Priority.P1;
  private static final AgentId AGENT_ID_1 = new AgentId();
  private static final InterventionId INTERVENTION_ID = new InterventionId();
  private static final InterventionId OTHER_INTERVENTION_ID = new InterventionId();
  private static final ZoneId ZONE_ID = new ZoneId("PLT200");
  private static final RoomId ROOM_ID = new RoomId("1");
  private static final RoomType ROOM_TYPE = RoomType.SERVER_ROOM;
  private static final boolean ELECTRIC_CLOSURE = true;
  private static final Idul LABORATORY_RESPONSIBLE_IDUL = new Idul("1");
  private static final Idul AN_IDUL = new Idul("2");
  private static final ActionBuilder NULL_ACTION_BUILDER = null;
  private static final BuildingId NULL_BUILDING_ID = null;
  private static final boolean IS_ON_FIRE = true;
  private static final ZoneId NULL_ZONE_ID = null;
  private static final Runnable NULL_ACTIVATE_FIRE_ALARM = null;
  private static final int FIVE_OCCUPANTS = 5;
  private static final boolean SUPPORTS_OCCUPATION_COUNTING = true;

  @Nested
  class AgentTests{
    private TestRoom requestedAgentsRoom;
    private TestRoom deployedAgentsRoom;
    private TestRoom arrivedAgentsRoom;

    @BeforeEach
    void setUpAgentsInRoom(){
      RequestAgent matchingAgentRequest = new RequestAgent(AGENT_ID_1,ZONE_ID,PRIORITY,
          INTERVENTION_ID,ROOM_ID);
      List<RequestAgent> agentsRequested = new ArrayList<>();
      agentsRequested.add(matchingAgentRequest);

      requestedAgentsRoom = new TestRoom(ROOM_ID,ROOM_TYPE,ELECTRIC_CLOSURE,
          LABORATORY_RESPONSIBLE_IDUL,agentsRequested,new ArrayList<>(),new ArrayList<>());

      Agent matchingDeployedAgent = new Agent(AGENT_ID_1,INTERVENTION_ID,PRIORITY);
      List<Agent> agentsDeployed = new ArrayList<>();
      agentsDeployed.add(matchingDeployedAgent);

      deployedAgentsRoom = new TestRoom(ROOM_ID,ROOM_TYPE,ELECTRIC_CLOSURE,
          LABORATORY_RESPONSIBLE_IDUL,new ArrayList<>(),agentsDeployed,new ArrayList<>());

      Agent matchingArrivedAgent = new Agent(AGENT_ID_1,INTERVENTION_ID,PRIORITY);
      List<Agent> agentsArrived = new ArrayList<>();
      agentsArrived.add(matchingArrivedAgent);

      arrivedAgentsRoom = new TestRoom(ROOM_ID,ROOM_TYPE,ELECTRIC_CLOSURE,
          LABORATORY_RESPONSIBLE_IDUL,new ArrayList<>(),new ArrayList<>(),agentsArrived);
    }

    @Test
    void givenMatchingDeployedAgent_whenRegisterAgentDeployed_thenMoveToDeployedAgents(){
      requestedAgentsRoom.registerAgentDeployed(INTERVENTION_ID);

      Agent deployedAgent = requestedAgentsRoom.getAgentsDeployedInRoom().getFirst();
      assertEquals(INTERVENTION_ID,deployedAgent.interventionId());
    }

    @Test
    void givenMatchingArrivedAgent_whenRegisterAgentArrived_thenMoveToArrivedAgents(){
      deployedAgentsRoom.registerAgentArrived(INTERVENTION_ID,NULL_ACTION_BUILDER,NULL_BUILDING_ID,
          NULL_ZONE_ID,!IS_ON_FIRE,NULL_ACTIVATE_FIRE_ALARM);

      Agent arrivedAgent = deployedAgentsRoom.getAgentsArrivedInRoom().getFirst();
      assertEquals(INTERVENTION_ID,arrivedAgent.interventionId());
    }

    @Test
    void givenNoMatchingDeployedAgent_whenRegisterAgentDeployed_thenDoNothing(){
      requestedAgentsRoom.registerAgentDeployed(OTHER_INTERVENTION_ID);

      assertTrue(requestedAgentsRoom.getAgentsDeployedInRoom().isEmpty());
    }

    @Test
    void givenNoMatchingArrivedAgent_whenRegisterAgentArrived_thenDoNothing(){
      deployedAgentsRoom.registerAgentArrived(OTHER_INTERVENTION_ID,NULL_ACTION_BUILDER,
          NULL_BUILDING_ID,NULL_ZONE_ID,!IS_ON_FIRE,NULL_ACTIVATE_FIRE_ALARM);

      assertTrue(deployedAgentsRoom.getAgentsArrivedInRoom().isEmpty());
    }

    @Test
    void givenMatchingDeployedAgent_whenRegisterAgentDeployed_thenLeaveRequestedAgents(){
      requestedAgentsRoom.registerAgentDeployed(INTERVENTION_ID);

      assertTrue(requestedAgentsRoom.getAgentsRequested().isEmpty());
    }

    @Test
    void givenMatchingArrivedAgent_whenRegisterAgentArrived_thenLeaveDeployedAgents(){
      deployedAgentsRoom.registerAgentArrived(INTERVENTION_ID,NULL_ACTION_BUILDER,NULL_BUILDING_ID,
          NULL_ZONE_ID,!IS_ON_FIRE,NULL_ACTIVATE_FIRE_ALARM);

      assertTrue(deployedAgentsRoom.getAgentsDeployedInRoom().isEmpty());
    }

    @Test
    void givenNoMatchingDeployedAgent_whenRegisterAgentDeployed_thenStayInRequestedAgents(){
      requestedAgentsRoom.registerAgentDeployed(OTHER_INTERVENTION_ID);

      assertFalse(requestedAgentsRoom.getAgentsRequested().isEmpty());
    }

    @Test
    void givenNoMatchingArrivedAgent_whenRegisterAgentArrived_thenStayInDeployedAgents(){
      deployedAgentsRoom.registerAgentArrived(OTHER_INTERVENTION_ID,NULL_ACTION_BUILDER,
          NULL_BUILDING_ID,NULL_ZONE_ID,!IS_ON_FIRE,NULL_ACTIVATE_FIRE_ALARM);

      assertFalse(deployedAgentsRoom.getAgentsDeployedInRoom().isEmpty());
    }

    @Test
    void givenMatchingArrivedAgent_whenRegisterAgentArrived_thenLeaveRequestedAgents(){
      requestedAgentsRoom.registerAgentArrived(INTERVENTION_ID,NULL_ACTION_BUILDER,NULL_BUILDING_ID,
          NULL_ZONE_ID,!IS_ON_FIRE,NULL_ACTIVATE_FIRE_ALARM);

      assertTrue(requestedAgentsRoom.getAgentsRequested().isEmpty());
    }

    @Test
    void givenMatchingLeavingAgent_whenRegisterAgentLeft_thenLeaveArrivedAgents(){
      arrivedAgentsRoom.registerAgentLeft(INTERVENTION_ID);

      assertTrue(arrivedAgentsRoom.getAgentsArrivedInRoom().isEmpty());
    }

    @Test
    void givenNoMatchingArrivedAgent_whenRegisterAgentLeft_thenNotLeaveArrivedAgents(){
      arrivedAgentsRoom.registerAgentLeft(OTHER_INTERVENTION_ID);

      assertFalse(arrivedAgentsRoom.getAgentsArrivedInRoom().isEmpty());
    }

    @Test
    void givenLaboratoryResponsibleAccess_whenCheckLaboratoryResponsible_thenAccessIsAllowed(){
      AccessStatus status = requestedAgentsRoom
          .authorizeIfUserLaboratoryResponsible(LABORATORY_RESPONSIBLE_IDUL);

      assertEquals(AccessStatus.ALLOWED,status);
    }

    @Test
    void givenUnknownUserAccess_whenCheckLaboratoryResponsible_thenAccessIsDenied(){
      AccessStatus status = requestedAgentsRoom.authorizeIfUserLaboratoryResponsible(AN_IDUL);

      assertEquals(AccessStatus.DENIED,status);
    }
  }

  @Nested
  class OccupationTests{
    private OccupationCounter occupationCounter;
    private Room room;

    @BeforeEach
    void setUpOccupation(){
      occupationCounter = mock(OccupationCounter.class);
      room = new TestRoom(occupationCounter);
    }

    @Test
    void whenIncrementIdentifiedUserOccupation_thenDelegatesToOccupationCounter(){
      room.incrementIdentifiedUserOccupation(AN_IDUL);

      verify(occupationCounter).incrementIdentifiedUserOccupation(AN_IDUL);
    }

    @Test
    void whenIncrementUnidentifiedUserOccupation_thenDelegatesToOccupationCounter(){
      room.incrementUnidentifiedUserOccupation();

      verify(occupationCounter).incrementUnidentifiedUserOccupation();
    }

    @Test
    void whenDecrementOccupation_thenDelegatesToOccupationCounter(){
      room.decrementOccupation();

      verify(occupationCounter).decrementOccupation();
    }

    @Test
    void whenSupportsOccupationCounting_thenDelegatesToOccupationCounter() {
      when(occupationCounter.supportsOccupationCounting()).thenReturn(SUPPORTS_OCCUPATION_COUNTING);

      assertTrue(room.supportsOccupationCounting());
      verify(occupationCounter).supportsOccupationCounting();
    }

    @Test
    void whenGetCurrentOccupation_thenDelegatesToOccupationCounter() {
      when(occupationCounter.getCurrentOccupation()).thenReturn(FIVE_OCCUPANTS);

      assertEquals(FIVE_OCCUPANTS, room.getCurrentOccupation());
      verify(occupationCounter).getCurrentOccupation();
    }

    @Test
    void whenGetConsecutiveAccessesByPerson_thenDelegatesToOccupationCounter(){
      Map<Idul, Integer> consecutiveAccessesByPerson = new HashMap<>();
      when(occupationCounter.getConsecutiveAccessesByPerson())
          .thenReturn(consecutiveAccessesByPerson);

      assertEquals(consecutiveAccessesByPerson,room.getConsecutiveAccessesByPerson());
      verify(occupationCounter).getConsecutiveAccessesByPerson();
    }
  }

  private static class TestRoom extends Room{
    public TestRoom(RoomId roomId,RoomType type,boolean supportsElectricClosure,
        Idul labSafetyResponsible,List<RequestAgent> agentsRequested,
        List<Agent> agentsDeployedInRoom,List<Agent> agentsArrivedRoom) {
      super(roomId, type, supportsElectricClosure, labSafetyResponsible, agentsRequested,
          agentsDeployedInRoom, agentsArrivedRoom);
    }

    public TestRoom(OccupationCounter occupationCounter) {
      super();
      this.occupationCounter = occupationCounter;
    }
  }
}
