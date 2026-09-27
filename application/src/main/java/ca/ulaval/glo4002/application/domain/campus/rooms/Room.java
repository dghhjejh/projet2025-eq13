package ca.ulaval.glo4002.application.domain.campus.rooms;

import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentInterventionContext;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.*;

public abstract class Room{
  protected RoomId id;

  protected RoomType type;

  protected boolean supportsElectricClosure;

  protected Idul labSafetyResponsible;
  protected OccupationCounter occupationCounter;

  protected final AgentInterventionContext agentContext;

  public Room() {
    this.occupationCounter = new OccupationCounter();
    this.agentContext = new AgentInterventionContext();
  }

  public Room(RoomId roomId,RoomType type,boolean supportsElectricClosure,Idul labSafetyResponsible,
      List<RequestAgent> agentsRequested,List<Agent> agentsDeployedInRoom,
      List<Agent> agentsArrivedInRoom) {
    this.id = roomId;
    this.type = type;
    this.supportsElectricClosure = supportsElectricClosure;
    this.labSafetyResponsible = labSafetyResponsible;
    this.agentContext = new AgentInterventionContext(agentsRequested,agentsDeployedInRoom,
        agentsArrivedInRoom);
    this.occupationCounter = new OccupationCounter();
  }

  public RoomId getId(){
    return this.id;
  }

  public void setId(RoomId id){
    this.id = id;
  }

  public RoomType getType(){
    return this.type;
  }

  public void setType(RoomType type){
    this.type = type;
  }

  public Idul getLabSafetyResponsible(){
    return this.labSafetyResponsible;
  }

  public List<Agent> getAgentsDeployedInRoom(){
    return agentContext.agentsDeployed();
  }

  public List<RequestAgent> getAgentsRequested(){
    return agentContext.agentsRequested();
  }

  public List<Agent> getAgentsArrivedInRoom(){
    return agentContext.agentsArrived();
  }

  public boolean getSupportsElectricClosure(){
    return this.supportsElectricClosure;
  }

  public void addDeployedAgent(Agent agent){
    agentContext.addDeployed(agent);
  }

  public int getAgentsArrivedCount(){
    return agentContext.getArrivedCount();
  }

  public void setOccupationCounter(OccupationCounter occupationCounter){
    this.occupationCounter = occupationCounter;
  }

  public void incrementIdentifiedUserOccupation(Idul idul){
    occupationCounter.incrementIdentifiedUserOccupation(idul);
  }

  public void incrementUnidentifiedUserOccupation(){
    occupationCounter.incrementUnidentifiedUserOccupation();
  }

  public void decrementOccupation(){
    occupationCounter.decrementOccupation();
  }

  public boolean supportsOccupationCounting(){
    return occupationCounter.supportsOccupationCounting();
  }

  public int getCurrentOccupation(){
    return occupationCounter.getCurrentOccupation();
  }

  public Map<Idul, Integer> getConsecutiveAccessesByPerson(){
    return occupationCounter.getConsecutiveAccessesByPerson();
  }

  public List<Action> triggerFireEmergency(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder,UserRepository userRepository,Set<Idul> notifiedResponsibles){
    return new ArrayList<>();
  }

  public List<Action> resolveFireExtinguished(BuildingId buildingId,ZoneId zoneId,
      ActionBuilder actionBuilder,UserRepository userRepository,Set<Idul> notifiedResponsibles){
    return new ArrayList<>();
  }

  public List<Action> registerEntryWithoutCard(BuildingId buildingId,ActionBuilder actionBuilder,
      ZoneId zoneId,Runnable activateFireAlarm){
    incrementUnidentifiedUserOccupation();
    return new ArrayList<>();
  }

  public void registerAgentDeployed(InterventionId interventionId){
    agentContext.registerDeploymentFromRequest(interventionId);
  }

  public List<Action> registerAgentArrived(InterventionId interventionId,
      ActionBuilder actionBuilder,BuildingId buildingId,ZoneId id,boolean isZoneOnFire,
      Runnable activateFireAlarm){

    agentContext.registerArrival(interventionId);

    return new ArrayList<>();
  }

  public void registerAgentLeft(InterventionId interventionId){
    agentContext.registerLeft(interventionId);
  }

  public AccessStatus authorizeIfUserLaboratoryResponsible(Idul idul){
    return idul.equals(labSafetyResponsible) ? AccessStatus.ALLOWED : AccessStatus.DENIED;
  }

  public int getNumberOfAgentsRequested(){
    return agentContext.getRequestCount();
  }
}
