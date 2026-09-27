package ca.ulaval.glo4002.application.domain.campus.zones;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.agents.AgentInterventionContext;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.gathering.GatheringId;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import java.util.*;

public abstract class Zone{
  public static final int DIVISION_FOR_NUMBER_AGENTS_WHEN_LARGE_GROUP = 12;
  public static final int DIVISION_FOR_NUMBER_AGENTS_WHEN_HAS_RISKY_OR_SOCIAL_GATHERING = 10;
  public static final int MINIMUM_NUMBER_OF_AGENTS_WHEN_HAS_RISKY_OR_SOCIAL_GATHERING = 5;
  protected static final int LARGE_GROUP_OCCUPATION_COUNT = 50;
  private static final int MAXIMUM_NUMBER_OF_AGENTS = 4;
  private static final int NUMBER_OF_AGENTS_TO_SEND_FOR_FIRE_PREALARM = 1;
  private static final int MINIMUM_NUMBER_OF_CONSECUTIVE_SECURITY_AGENTS_ACCESS_REQUESTS = 3;
  protected final Priority PRIORITY_FOR_FIRES = Priority.P1;
  private final AgentInterventionContext agentContext;
  private final List<Gathering> gatheringsInZone;
  private int numberOfConsecutiveSecurityAgentsAccessRequests = 0;
  private Idul lastAccessRequestIdul = null;

  private ZoneId id;
  private ZoneType type;
  private boolean currentlyHasActivity;
  private List<Room> rooms;
  private List<Door> doors;
  private UrgencyState urgencyState;

  private int smokeConcentration = 0;

  private boolean areAlarmsActive = false;

  private VentilationSystem ventilationSystem;

  public Zone() {
    this.agentContext = new AgentInterventionContext();
    this.gatheringsInZone = new ArrayList<>();
    this.urgencyState = UrgencyState.NORMAL;
  }

  public Zone(ZoneId zoneId,ZoneType type,boolean currentlyHasActivity,List<Door> doors,
      List<Room> rooms,UrgencyState urgencyState,int smokeConcentration,boolean hasActiveAlarms,
      VentilationSystem ventilationSystem,List<RequestAgent> agentsRequested,
      List<Agent> agentsDeployedInZone,List<Agent> agentsArrivedInZone,
      List<Gathering> gatheringsInZone) {
    this.id = zoneId;
    this.type = type;
    this.currentlyHasActivity = currentlyHasActivity;
    this.urgencyState = urgencyState;
    this.smokeConcentration = smokeConcentration;
    this.areAlarmsActive = hasActiveAlarms;
    this.doors = doors;
    this.rooms = rooms;
    this.gatheringsInZone = gatheringsInZone;
    this.ventilationSystem = ventilationSystem;
    this.agentContext = new AgentInterventionContext(agentsRequested,agentsDeployedInZone,
        agentsArrivedInZone);
  }

  public ZoneId getId(){
    return this.id;
  }

  public void setId(ZoneId id){
    this.id = id;
  }

  public ZoneType getType(){
    return this.type;
  }

  public void setType(ZoneType type){
    this.type = type;
  }

  public boolean getCurrentlyHasActivity(){
    return this.currentlyHasActivity;
  }

  public void setCurrentlyHasActivity(boolean currentlyHasActivity){
    this.currentlyHasActivity = currentlyHasActivity;
  }

  public List<Room> getRooms(){
    return this.rooms;
  }

  public void setRooms(List<Room> rooms){
    this.rooms = rooms;
  }

  public void addRoom(Room newRoom){
    this.rooms.add(newRoom);
  }

  public List<Door> getDoors(){
    return this.doors;
  }

  public void setDoors(List<Door> doors){
    this.doors = doors;
  }

  public void addDoor(Door newDoor){
    this.doors.add(newDoor);
  }

  public void setSmokeConcentrationLevel(int concentrationLevel){
    this.smokeConcentration = concentrationLevel;
  }

  public int getSmokeConcentration(){
    return this.smokeConcentration;
  }

  public void setAreAlarmsActive(boolean areAlarmsActive){
    this.areAlarmsActive = areAlarmsActive;
  }

  public List<RequestAgent> getAgentsRequested(){
    return agentContext.agentsRequested();
  }

  public List<Agent> getAgentsDeployedInZone(){
    return agentContext.agentsDeployed();
  }

  public int getNumberOfAgentsDeployed(){
    return agentContext.getDeployedCount();
  }

  public List<Agent> getAgentsArrivedInZone(){
    return agentContext.agentsArrived();
  }

  public int getNumberOfAgentsArrived(){
    return agentContext.getArrivedCount();
  }

  public VentilationSystem getVentilationSystem(){
    return this.ventilationSystem;
  }

  public void setVentilationSystem(VentilationSystem ventilationSystem){
    this.ventilationSystem = ventilationSystem;
  }

  public void addGathering(Gathering gathering){
    this.gatheringsInZone.add(gathering);
  }

  public void addArrivedAgents(Agent agent){
    agentContext.addArrived(agent);
  }

  public void addDeployedAgents(Agent agent){
    agentContext.addDeployed(agent);
  }

  public abstract List<Action> triggerFireEmergency(BuildingId buildingId,
      ActionBuilder actionBuilder,UserRepository userRepository);

  public int getNumberOfAgentsRequestedInZoneAndAllRooms(){
    int totalRequested = getNumberOfAgentsRequested();

    for (Room room : this.rooms){
      totalRequested += room.getNumberOfAgentsRequested();
    }
    return totalRequested;
  }

  public int getNumberOfAgentsRequested(){
    return agentContext.getRequestCount();
  }

  public abstract List<Action> triggerFireEmergencyInOtherZones(BuildingId buildingId,
      ActionBuilder actionBuilder);

  public List<Action> resolveFireExtinguished(BuildingId buildingId,ActionBuilder actionBuilder,
      UserRepository userRepository){
    List<Action> actionsToSend = new ArrayList<>();
    Set<Idul> notifiedResponsibles = new HashSet<>();

    for (Room room : this.rooms){
      actionsToSend.addAll(room.resolveFireExtinguished(buildingId,this.id,actionBuilder,
          userRepository,notifiedResponsibles));
    }
    this.urgencyState = UrgencyState.NORMAL;
    return actionsToSend;
  }

  public abstract List<Action> triggerFirePrealarm(BuildingId buildingId,
      ActionBuilder actionBuilder);

  public abstract List<Action> resolveVentilationForFireExtinguished(BuildingId buildingId,
      ActionBuilder actionBuilder);

  public List<Action> activateFireAlarms(BuildingId buildingId,ActionBuilder actionBuilder){
    List<Action> actions = new ArrayList<>();

    if (!this.areAlarmsActive){
      actions.add(actionBuilder.createActivateFireAlarmAction(buildingId,this.id,true));
      this.areAlarmsActive = true;
    }

    return actions;
  }

  public List<Action> deactivateFireAlarms(BuildingId buildingId,ActionBuilder actionBuilder){
    List<Action> actions = new ArrayList<>();

    if (this.areAlarmsActive){
      actions.add(actionBuilder.createActivateFireAlarmAction(buildingId,this.id,false));
      this.areAlarmsActive = false;
    }

    return actions;
  }

  public boolean areAlarmsActive(){
    return this.areAlarmsActive;
  }

  public void registerAgentDeployment(InterventionId interventionId){
    if (agentContext.registerDeploymentFromRequest(interventionId)){
      return;
    }

    registerAgentDeployedForAllRooms(interventionId);
  }

  private void registerAgentDeployedForAllRooms(InterventionId interventionId){
    for (Room room : this.rooms){
      room.registerAgentDeployed(interventionId);
    }
  }

  public void registerRoomEntry(RoomId roomId,AccessCard accessCard){
    if (accessCard == null){
      getRoomInZone(roomId).ifPresent(Room::incrementUnidentifiedUserOccupation);
    } else{
      getRoomInZone(roomId)
          .ifPresent(room -> room.incrementIdentifiedUserOccupation(accessCard.idul()));
    }
  }

  protected Optional<Room> getRoomInZone(RoomId roomId){
    if (roomId == null){
      return Optional.empty();
    }
    return this.rooms.stream().filter(room -> roomId.equals(room.getId())).findFirst();
  }

  public List<Action> registerRoomExit(RoomId roomId,BuildingId buildingId,
      ActionBuilder actionBuilder){
    getRoomInZone(roomId).ifPresent(Room::decrementOccupation);
    return new ArrayList<>();
  }

  public List<Action> registerEntryWithoutCard(BuildingId buildingId,RoomId roomId,
      ActionBuilder actionBuilder){
    return getRoomInZone(roomId).map(room -> room.registerEntryWithoutCard(buildingId,actionBuilder,
        this.id,() -> this.areAlarmsActive = true)).orElseGet(List::of);
  }

  public List<Action> registerAgentArrivedAtIntervention(InterventionId interventionId,
      ActionBuilder actionBuilder,BuildingId buildingId){
    List<Action> actionsToSend = new ArrayList<>();

    if (agentContext.registerArrival(interventionId)){
      return actionsToSend;
    }

    actionsToSend.addAll(
        registerAgentArrivedAtInterventionForAllRooms(interventionId,actionBuilder,buildingId));

    return actionsToSend;
  }

  protected List<Action> registerAgentArrivedAtInterventionForAllRooms(
      InterventionId interventionId,ActionBuilder actionBuilder,BuildingId buildingId){
    List<Action> actions = new ArrayList<>();

    for (Room room : this.rooms){
      actions.addAll(room.registerAgentArrived(interventionId,actionBuilder,buildingId,this.id,
          isOnFire(),() -> this.areAlarmsActive = false));
    }
    return actions;
  }

  public boolean isOnFire(){
    return this.urgencyState.equals(UrgencyState.FIRE);
  }

  public void registerAgentLeftIntervention(InterventionId interventionId){
    if (agentContext.registerLeft(interventionId)){
      return;
    }
    registerAgentLeftInterventionForAllRooms(interventionId);
  }

  protected void registerAgentLeftInterventionForAllRooms(InterventionId interventionId){
    for (Room room : this.rooms){
      room.registerAgentLeft(interventionId);
    }
  }

  public List<Action> applyRulesForDoorsDuringFire(ActionBuilder actionBuilder,
      BuildingId buildingId){
    List<Action> actionsToSend = new ArrayList<>();

    for (Door door : this.doors){
      actionsToSend.addAll(door.triggerFireEmergency(actionBuilder,buildingId,this.id));
    }
    return actionsToSend;
  }

  public List<Action> applyRulesForDoorsWhenExtinguished(ActionBuilder actionBuilder,
      BuildingId buildingId){
    List<Action> actionsToSend = new ArrayList<>();

    if (!isOnFire()){
      for (Door door : this.doors){
        actionsToSend.addAll(door.resolveFireExtinguished(actionBuilder,buildingId,this.id));
      }
    }
    return actionsToSend;
  }

  public void addContextForAccessRules(AccessCard accessCard,AccessContext accessContext,
      RoomId roomId,DoorId doorId){

    accessContext.setSpecifiedZoneHasFireOrPrealarm(
        isOnFire() || getUrgencyState() == UrgencyState.PREALARM);

    if (roomId != null){
      accessContext.setUserIsTheLabResponsibleForSpecifiedRoom(accessCard != null
          && checkIfUserIsLabResponsibleForSpecifiedRoom(accessCard.idul(),roomId));
    }
    Optional<Door> door = getDoorInZone(doorId);

    door.ifPresent(d -> d.addContextForAccessRules(accessContext));
  }

  public UrgencyState getUrgencyState(){
    return this.urgencyState;
  }

  public void setUrgencyState(UrgencyState urgencyState){
    this.urgencyState = urgencyState;
  }

  public boolean checkIfUserIsLabResponsibleForSpecifiedRoom(Idul idul,RoomId roomId){
    Optional<Room> room = getRoomInZone(roomId);
    return room.map(r -> r.authorizeIfUserLaboratoryResponsible(idul).equals(AccessStatus.ALLOWED))
        .orElse(false);
  }

  public boolean checkIfUserIsLabResponsibleForAnyRoom(Idul idul){
    return this.rooms.stream().anyMatch(
        room -> room.authorizeIfUserLaboratoryResponsible(idul).equals(AccessStatus.ALLOWED));
  }

  private Optional<Door> getDoorInZone(DoorId doorId){
    if (doorId == null){
      return Optional.empty();
    }
    return this.doors.stream().filter(door -> doorId.equals(door.getId())).findFirst();
  }

  public List<Action> registerGatheringStarted(Gathering gathering,BuildingId buildingId,
      ActionBuilder actionBuilder){
    this.gatheringsInZone.add(gathering);
    return gathering.determineSecurityActionsForGatheringStarted(this,buildingId,actionBuilder);
  }

  public List<Action> registerGatheringEnded(GatheringId gatheringId,BuildingId buildingId,
      ActionBuilder actionBuilder){
    for (Gathering gathering : this.gatheringsInZone){
      if (gathering.getGatheringId().equals(gatheringId)){
        this.gatheringsInZone.remove(gathering);
        return gathering.determineActionsForGatheringEnded(this,buildingId,actionBuilder);
      }
    }
    return new ArrayList<>();
  }

  public List<Gathering> getGatheringsInZone(){
    return this.gatheringsInZone;
  }

  public List<Action> sendAgentsForGathering(int numberOfAgentsToSend,Priority priority,
      ActionBuilder actionBuilder){
    return sendAgents(actionBuilder,numberOfAgentsToSend,priority);
  }

  protected List<Action> sendAgents(ActionBuilder actionBuilder,int numberOfAgentsToSend,
      Priority priority){
    List<Action> actionsToSend = new ArrayList<>();
    for (int i = 0; i < numberOfAgentsToSend; i++){
      Action action = actionBuilder.createRequestAgentAction(this.id,priority);
      agentContext.addRequested((RequestAgent) action);
      actionsToSend.add(action);
    }
    return actionsToSend;
  }

  public int getTotalOccupation(){
    return getOccupationForRooms() + getGatheringOccupation() + getTotalAgentsArrived();
  }

  public int getOccupationForRooms(){
    int totalOccupationForRooms = 0;
    for (Room room : this.rooms){
      totalOccupationForRooms += room.getCurrentOccupation();
    }
    return totalOccupationForRooms;
  }

  private int getGatheringOccupation(){
    int totalGatheringOccupation = 0;
    for (Gathering gathering : this.gatheringsInZone){
      totalGatheringOccupation += gathering.getExpectedAttendees();
    }
    return totalGatheringOccupation;
  }

  private int getTotalAgentsArrived(){
    return getNumberOfAgentsArrivedInZone()
        + rooms.stream().mapToInt(Room::getAgentsArrivedCount).sum();
  }

  public int getNumberOfAgentsArrivedInZone(){
    return agentContext.getArrivedCount();
  }

  public List<String> getIdentifiedOccupants(){
    Set<String> occupants = new HashSet<>();
    for (Room room : this.rooms){
      for (Idul idul : room.getConsecutiveAccessesByPerson().keySet()){
        occupants.add(idul.toString());
      }
    }
    return new ArrayList<>(occupants);
  }

  public Map<String, Integer> getConsecutiveAccessesByPerson(){
    Map<String, Integer> allAccesses = new HashMap<>();
    for (Room room : this.rooms){
      Map<Idul, Integer> roomAccesses = room.getConsecutiveAccessesByPerson();
      for (Map.Entry<Idul, Integer> entry : roomAccesses.entrySet()){
        String idulStr = entry.getKey().toString();
        allAccesses.merge(idulStr,entry.getValue(),Integer::sum);
      }
    }
    return allAccesses;
  }

  public List<Action> applyRulesForDoorsDuringGathering(BuildingId buildingId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    for (Door door : this.doors){
      List<Action> actions = door.applyGatheringStarted(actionBuilder,buildingId,this.id);
      actionsToSend.addAll(actions);
    }
    return actionsToSend;
  }

  public List<Action> applyRulesForDoorsWhenGatheringEnds(BuildingId buildingId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    for (Door door : this.doors){
      List<Action> actions = door.applyGatheringEnded(actionBuilder,buildingId,this.id);
      actionsToSend.addAll(actions);
    }
    return actionsToSend;
  }

  public boolean hasLargeGroup(){
    return getGatheringOccupation() >= LARGE_GROUP_OCCUPATION_COUNT;
  }

  private boolean hasSocialOrRiskyGathering(){
    return gatheringsInZone.stream().anyMatch(Gathering::isSocialOrRisky);
  }

  protected int getNumberOfAgentsToSendForFire(){
    if (hasLargeGroup()){
      int needed = getTotalAgentsNeededWhenLargeGroup();
      return Math.max(0,needed);
    }

    if (hasSocialOrRiskyGathering()){
      int needed = getTotalAgentsNeededWhenZoneHasSocialOrRiskyGathering();
      return Math.max(0,needed);
    }

    return Math.max(MAXIMUM_NUMBER_OF_AGENTS - getNumberOfAgentsDeployedInZone(),0);
  }

  private int getTotalAgentsNeededWhenLargeGroup(){
    return (getGatheringOccupation() / DIVISION_FOR_NUMBER_AGENTS_WHEN_LARGE_GROUP)
        - getTotalAgentsArrived();
  }

  private int getTotalAgentsNeededWhenZoneHasSocialOrRiskyGathering(){
    int required = Math.max(MINIMUM_NUMBER_OF_AGENTS_WHEN_HAS_RISKY_OR_SOCIAL_GATHERING,
        getGatheringOccupation() / DIVISION_FOR_NUMBER_AGENTS_WHEN_HAS_RISKY_OR_SOCIAL_GATHERING);
    return required - getTotalAgentsArrived();
  }

  protected int getNumberOfAgentsToSendForFirePrealarm(){
    return Math.max(NUMBER_OF_AGENTS_TO_SEND_FOR_FIRE_PREALARM - getNumberOfAgentsDeployedInZone(),
        0);
  }

  public int getNumberOfAgentsDeployedInZone(){
    return agentContext.getDeployedCount();
  }

  public AccessStatus evaluateAccessInCaseOfExtremeNegativePressure(AccessCard accessCard){
    if (this.ventilationSystem.hasExtremeNegativePressure()){
      return extremeNegativePressureAuthorization(accessCard);
    }
    this.numberOfConsecutiveSecurityAgentsAccessRequests = 0;
    this.lastAccessRequestIdul = null;
    return AccessStatus.ALLOWED;
  }

  private AccessStatus extremeNegativePressureAuthorization(AccessCard accessCard){
    boolean isSecurityAgent = accessCard != null && accessCard.isSecurityAgent();
    if (!isSecurityAgent){
      this.numberOfConsecutiveSecurityAgentsAccessRequests = 0;
      return AccessStatus.DENIED;
    }

    Idul securityAgentIdul = accessCard.idul();
    if (!securityAgentIdul.equals(this.lastAccessRequestIdul)){
      this.numberOfConsecutiveSecurityAgentsAccessRequests = 1;
      this.lastAccessRequestIdul = securityAgentIdul;
    } else{
      this.numberOfConsecutiveSecurityAgentsAccessRequests++;
    }

    if (this.numberOfConsecutiveSecurityAgentsAccessRequests < MINIMUM_NUMBER_OF_CONSECUTIVE_SECURITY_AGENTS_ACCESS_REQUESTS){
      return AccessStatus.DENIED;
    }

    this.numberOfConsecutiveSecurityAgentsAccessRequests = 0;
    this.lastAccessRequestIdul = null;
    return AccessStatus.ALLOWED;
  }
}
