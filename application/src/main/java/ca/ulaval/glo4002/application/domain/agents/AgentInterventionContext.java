package ca.ulaval.glo4002.application.domain.agents;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import java.util.*;

public record AgentInterventionContext(List<RequestAgent> agentsRequested,
    List<Agent> agentsDeployed, List<Agent> agentsArrived) {
  public AgentInterventionContext() {
    this(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
  }

  public AgentInterventionContext(List<RequestAgent> agentsRequested,List<Agent> agentsDeployed,
      List<Agent> agentsArrived) {
    this.agentsRequested = new ArrayList<>(agentsRequested);
    this.agentsDeployed = new ArrayList<>(agentsDeployed);
    this.agentsArrived = new ArrayList<>(agentsArrived);
  }

  @Override
  public List<RequestAgent> agentsRequested(){
    return Collections.unmodifiableList(agentsRequested);
  }

  @Override
  public List<Agent> agentsDeployed(){
    return Collections.unmodifiableList(agentsDeployed);
  }

  @Override
  public List<Agent> agentsArrived(){
    return Collections.unmodifiableList(agentsArrived);
  }

  public int getRequestCount(){
    return agentsRequested.size();
  }

  public int getDeployedCount(){
    return agentsDeployed.size();
  }

  public int getArrivedCount(){
    return agentsArrived.size();
  }

  public void addRequested(RequestAgent requestAgent){
    agentsRequested.add(requestAgent);
  }

  public void addDeployed(Agent agent){
    agentsDeployed.add(agent);
  }

  public void addArrived(Agent agent){
    agentsArrived.add(agent);
  }

  public Optional<Agent> getAgentIfDeployed(InterventionId interventionId){
    return agentsDeployed.stream().filter(agent -> agent.interventionId().equals(interventionId))
        .findFirst();
  }

  public Optional<Agent> getAgentIfArrived(InterventionId interventionId){
    return agentsArrived.stream().filter(agent -> agent.interventionId().equals(interventionId))
        .findFirst();
  }

  public Optional<RequestAgent> getAgentIfRequested(InterventionId interventionId){
    return agentsRequested.stream().filter(agent -> agent.interventionId().equals(interventionId))
        .findFirst();
  }

  public boolean registerDeploymentFromRequest(InterventionId interventionId){
    Optional<RequestAgent> requested = getAgentIfRequested(interventionId);

    if (requested.isEmpty()){
      return false;
    }

    RequestAgent requestAgent = requested.get();
    agentsRequested.remove(requestAgent);
    agentsDeployed.add(requestAgent.getCorrespondingAgent());
    return true;
  }

  public boolean registerArrival(InterventionId interventionId){
    Optional<Agent> deployed = getAgentIfDeployed(interventionId);

    if (deployed.isPresent()){
      Agent agent = deployed.get();
      agentsDeployed.remove(agent);
      agentsArrived.add(agent);
      return true;
    }

    Optional<RequestAgent> requested = getAgentIfRequested(interventionId);

    if (requested.isPresent()){
      RequestAgent requestAgent = requested.get();
      agentsRequested.remove(requestAgent);
      agentsArrived.add(requestAgent.getCorrespondingAgent());
      return true;
    }

    return false;
  }

  public boolean registerLeft(InterventionId interventionId){
    Optional<Agent> arrived = getAgentIfArrived(interventionId);

    if (arrived.isPresent()){
      agentsArrived.remove(arrived.get());
      return true;
    }

    return false;
  }
}
