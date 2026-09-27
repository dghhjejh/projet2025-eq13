package ca.ulaval.glo4002.application.domain.campus;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentId;
import ca.ulaval.glo4002.application.domain.agents.AgentInterventionContext;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AgentInterventionContextTest{

  private static final Priority PRIORITY = Priority.P1;
  private static final AgentId AGENT_ID_1 = new AgentId();
  private static final InterventionId INTERVENTION_ID = new InterventionId();
  private static final InterventionId OTHER_INTERVENTION_ID = new InterventionId();
  private static final ZoneId ZONE_ID = new ZoneId("PLT200");
  private static final RoomId ROOM_ID = new RoomId("1");
  private static final int EXPECTED_NUMBER_OF_AGENTS = 1;
  private static final int NO_AGENTS = 0;

  private AgentInterventionContext requestedAgentsContext;
  private AgentInterventionContext deployedAgentsContext;
  private AgentInterventionContext arrivedAgentsContext;

  @BeforeEach
  void setUpContexts(){
    RequestAgent matchingAgentRequest = new RequestAgent(AGENT_ID_1,ZONE_ID,PRIORITY,
        INTERVENTION_ID,ROOM_ID);
    List<RequestAgent> agentsRequested = new ArrayList<>();
    agentsRequested.add(matchingAgentRequest);
    requestedAgentsContext = new AgentInterventionContext(agentsRequested,new ArrayList<>(),
        new ArrayList<>());

    Agent matchingDeployedAgent = new Agent(AGENT_ID_1,INTERVENTION_ID,PRIORITY);
    List<Agent> agentsDeployed = new ArrayList<>();
    agentsDeployed.add(matchingDeployedAgent);
    deployedAgentsContext = new AgentInterventionContext(new ArrayList<>(),agentsDeployed,
        new ArrayList<>());

    Agent matchingArrivedAgent = new Agent(AGENT_ID_1,INTERVENTION_ID,PRIORITY);
    List<Agent> agentsArrived = new ArrayList<>();
    agentsArrived.add(matchingArrivedAgent);
    arrivedAgentsContext = new AgentInterventionContext(new ArrayList<>(),new ArrayList<>(),
        agentsArrived);
  }

  @Test
  void givenMatchingRequestedAgent_whenRegisterDeploymentFromRequest_thenMoveToDeployedAgents(){
    boolean deployed = requestedAgentsContext.registerDeploymentFromRequest(INTERVENTION_ID);

    assertTrue(deployed);
    assertEquals(NO_AGENTS,requestedAgentsContext.getRequestCount());
    assertEquals(EXPECTED_NUMBER_OF_AGENTS,requestedAgentsContext.getDeployedCount());
    assertTrue(requestedAgentsContext.getAgentIfDeployed(INTERVENTION_ID).isPresent());
  }

  @Test
  void givenNoMatchingRequestedAgent_whenRegisterDeploymentFromRequest_thenDoNothing(){
    boolean deployed = requestedAgentsContext.registerDeploymentFromRequest(OTHER_INTERVENTION_ID);

    assertFalse(deployed);
    assertEquals(EXPECTED_NUMBER_OF_AGENTS,requestedAgentsContext.getRequestCount());
    assertEquals(NO_AGENTS,requestedAgentsContext.getDeployedCount());
  }

  @Test
  void givenMatchingDeployedAgent_whenRegisterArrival_thenMoveToArrivedAgents(){
    boolean arrived = deployedAgentsContext.registerArrival(INTERVENTION_ID);

    assertTrue(arrived);
    assertEquals(NO_AGENTS,deployedAgentsContext.getDeployedCount());
    assertEquals(EXPECTED_NUMBER_OF_AGENTS,deployedAgentsContext.getArrivedCount());
    assertTrue(deployedAgentsContext.getAgentIfArrived(INTERVENTION_ID).isPresent());
  }

  @Test
  void givenNoMatchingDeployedAgent_whenRegisterArrival_thenDoNothing(){
    boolean arrived = deployedAgentsContext.registerArrival(OTHER_INTERVENTION_ID);

    assertFalse(arrived);
    assertEquals(EXPECTED_NUMBER_OF_AGENTS,deployedAgentsContext.getDeployedCount());
    assertEquals(NO_AGENTS,deployedAgentsContext.getArrivedCount());
  }

  @Test
  void givenMatchingRequestedAgent_whenRegisterArrival_thenMoveRequestedToArrivedAgents(){
    boolean arrived = requestedAgentsContext.registerArrival(INTERVENTION_ID);

    assertTrue(arrived);
    assertEquals(NO_AGENTS,requestedAgentsContext.getRequestCount());
    assertEquals(EXPECTED_NUMBER_OF_AGENTS,requestedAgentsContext.getArrivedCount());
    assertTrue(requestedAgentsContext.getAgentIfArrived(INTERVENTION_ID).isPresent());
  }

  @Test
  void givenNoMatchingRequestedAgent_whenRegisterArrival_thenStayInRequestedAgents(){
    boolean arrived = requestedAgentsContext.registerArrival(OTHER_INTERVENTION_ID);

    assertFalse(arrived);
    assertEquals(EXPECTED_NUMBER_OF_AGENTS,requestedAgentsContext.getRequestCount());
    assertEquals(NO_AGENTS,requestedAgentsContext.getArrivedCount());
  }

  @Test
  void givenMatchingArrivedAgent_whenRegisterLeft_thenLeaveArrivedAgents(){
    boolean left = arrivedAgentsContext.registerLeft(INTERVENTION_ID);

    assertTrue(left);
    assertEquals(NO_AGENTS,arrivedAgentsContext.getArrivedCount());
  }

  @Test
  void givenNoMatchingArrivedAgent_whenRegisterLeft_thenNotLeaveArrivedAgents(){
    boolean left = arrivedAgentsContext.registerLeft(OTHER_INTERVENTION_ID);

    assertFalse(left);
    assertEquals(EXPECTED_NUMBER_OF_AGENTS,arrivedAgentsContext.getArrivedCount());
    assertTrue(arrivedAgentsContext.getAgentIfArrived(INTERVENTION_ID).isPresent());
  }
}
