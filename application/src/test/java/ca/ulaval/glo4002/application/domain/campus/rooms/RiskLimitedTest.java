package ca.ulaval.glo4002.application.domain.campus.rooms;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.domain.actions.Action;
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
import java.util.List;
import java.util.Objects;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RiskLimitedTest{
  private static final BuildingId BUILDING_ID = new BuildingId("1");
  private static final ZoneId ZONE_ID = new ZoneId("PLT200");
  private static final RoomId ROOM_ID = new RoomId("PLT200-ROOM");
  private static final Idul LAB_RESPONSIBLE_IDUL = new Idul("1");
  private static final InterventionId INTERVENTION_ID = new InterventionId();
  private static final AgentId AGENT_ID = new AgentId();
  private static final Priority PRIORITY = Priority.P1;
  private static final int NUMBER_AGENT_ARRIVED = 1;
  private static final int NO_AGENT = 0;
  private static final Agent AGENT = new Agent(AGENT_ID,INTERVENTION_ID,PRIORITY);
  private static final boolean SUPPORTS_ELECTRICITY_CLOSURE = true;
  private static final boolean IS_ON_FIRE = true;
  private final boolean[] alarmActivated = new boolean[]{false};
  private final Runnable activateAlarm = () -> alarmActivated[0] = true;

  private ActionBuilder actionBuilder;
  private RiskLimited riskLimitedRoom;

  @BeforeEach
  void setup(){
    actionBuilder = new ActionBuilder();
    List<RequestAgent> requestAgents = new ArrayList<>();
    List<Agent> agentsDeployed = new ArrayList<>();
    List<Agent> agentsArrived = new ArrayList<>();
    riskLimitedRoom = new RiskLimited(ROOM_ID,SUPPORTS_ELECTRICITY_CLOSURE,LAB_RESPONSIBLE_IDUL,
        requestAgents,agentsDeployed,agentsArrived);
  }

  @Test
  void whenHandleEntry_WithoutCard_thenRequestsAgentInRoom(){
    Runnable activateAlarm = () -> {
    };

    List<Action> actions = riskLimitedRoom.registerEntryWithoutCard(BUILDING_ID,actionBuilder,
        ZONE_ID,activateAlarm);

    assertTrue(containsRequestAgentWith(actions));
    assertFalse(riskLimitedRoom.getAgentsRequested().isEmpty());
  }

  @Test
  void whenAgentArrivedInRoomMatched_whenRegisterAgentArrivedAtIntervention_thenAddAgentArrived(){
    riskLimitedRoom.addDeployedAgent(AGENT);

    riskLimitedRoom.registerAgentArrived(INTERVENTION_ID,actionBuilder,BUILDING_ID,ZONE_ID,
        !IS_ON_FIRE,activateAlarm);

    assertEquals(NUMBER_AGENT_ARRIVED,riskLimitedRoom.getAgentsArrivedInRoom().size());
  }

  @Test
  void whenAgentArrivedInRoomMatched_whenRegisterAgentArrivedAtIntervention_thenDeployedAgentIsRemoved(){
    riskLimitedRoom.addDeployedAgent(AGENT);

    riskLimitedRoom.registerAgentArrived(INTERVENTION_ID,actionBuilder,BUILDING_ID,ZONE_ID,
        !IS_ON_FIRE,activateAlarm);

    assertEquals(NO_AGENT,riskLimitedRoom.getAgentsDeployedInRoom().size());
  }

  private static boolean containsRequestAgentWith(List<Action> actions){
    return actions.stream().filter(a -> a instanceof RequestAgent).map(a -> (RequestAgent) a)
        .anyMatch(requestAgent -> requestAgent.priority() == Priority.P1
            && Objects.equals(requestAgent.zoneId(),RiskLimitedTest.ZONE_ID)
            && Objects.equals(requestAgent.roomId(),RiskLimitedTest.ROOM_ID));
  }
}
