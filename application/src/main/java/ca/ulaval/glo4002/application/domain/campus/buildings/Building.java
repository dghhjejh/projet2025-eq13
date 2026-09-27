package ca.ulaval.glo4002.application.domain.campus.buildings;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessEvaluator;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.enums.FireFighterCallReason;
import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.domain.event.SecurityEventName;
import ca.ulaval.glo4002.application.domain.event.parameters.EventParameter;
import ca.ulaval.glo4002.application.domain.event.parameters.EventParameterReader;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.gathering.GatheringFactory;
import ca.ulaval.glo4002.application.domain.gathering.GatheringId;
import ca.ulaval.glo4002.application.domain.gathering.GatheringType;
import java.util.*;

public class Building{
  public static final int SMOKE_THRESHOLD_1 = 20;
  public static final int SMOKE_THRESHOLD_2 = 50;
  public static final int SMOKE_THRESHOLD_3 = 80;
  private final SmokeThreshold smokeThreshold;
  private final EventParameterReader parameterReader;
  private final AccessEvaluator accessEvaluator;
  private BuildingId id;
  private String name;
  private PostalAddress postalAddress;
  private VentilationSystemType ventilationSystemType;
  private List<Zone> zones;

  public Building(BuildingId buildingId,String name,PostalAddress postalAddress,
      VentilationSystemType ventilationSystemType,List<Zone> zones,
      AccessEvaluator accessEvaluator) {
    this.id = buildingId;
    this.name = name;
    this.postalAddress = postalAddress;
    this.ventilationSystemType = ventilationSystemType;
    this.smokeThreshold = new SmokeThreshold(SMOKE_THRESHOLD_1,SMOKE_THRESHOLD_2,SMOKE_THRESHOLD_3);
    this.parameterReader = new EventParameterReader();
    this.accessEvaluator = accessEvaluator;
    this.zones = zones;
  }

  public BuildingId getId(){
    return this.id;
  }

  public void setId(BuildingId id){
    this.id = id;
  }

  public String getName(){
    return this.name;
  }

  public void setName(String name){
    this.name = name;
  }

  public PostalAddress getPostalAddress(){
    return this.postalAddress;
  }

  public void setPostalAddress(PostalAddress postalAddress){
    this.postalAddress = postalAddress;
  }

  public VentilationSystemType getVentilationSystem(){
    return this.ventilationSystemType;
  }

  public List<Zone> getZones(){
    return this.zones;
  }

  public void setZones(List<Zone> zones){
    this.zones = zones;
  }

  public void addZone(Zone newZone){
    this.zones.add(newZone);
  }

  public List<Action> handleEvent(ZoneId zoneId,SecurityEventName eventName,
      List<EventParameter> parameters,ActionBuilder actionBuilder,UserRepository userRepository){
    List<Action> actionsToSend = new ArrayList<>();

    Optional<Zone> fetchedZone = getZoneInBuilding(zoneId);

    if (fetchedZone.isEmpty())
      return actionsToSend;

    Zone specifiedZone = fetchedZone.get();

    switch (eventName){
      case SecurityEventName.FIRE, SecurityEventName.EXPIRED_PREALARM,
          SecurityEventName.CONFIRMED_PREALARM ->
        actionsToSend.addAll(triggerFireEmergencyIn(specifiedZone,actionBuilder,userRepository));
      case SecurityEventName.FIRE_ENDED ->
        actionsToSend.addAll(triggerFireExtinguishedIn(specifiedZone,actionBuilder,userRepository));
      case SecurityEventName.FIRE_PREALARM -> actionsToSend
          .addAll(evaluatePrealarmSituationIn(specifiedZone,actionBuilder,userRepository));
      case SecurityEventName.SMOKE_PRESENCE -> actionsToSend
          .addAll(processSmokeLevelsIn(specifiedZone,parameters,actionBuilder,userRepository));
      case SecurityEventName.AGENT_DEPLOYED_TO_INTERVENTION ->
        registerAgentDeploymentIn(specifiedZone,parameters);
      case SecurityEventName.AGENT_ARRIVED_AT_INTERVENTION ->
        actionsToSend.addAll(registerAgentArrivalIn(specifiedZone,parameters,actionBuilder));
      case SecurityEventName.AGENT_LEFT_INTERVENTION ->
        registerAgentLeavingIn(specifiedZone,parameters);
      case SecurityEventName.NO_CARD_ENTRY -> actionsToSend
          .addAll(reportEntryWithoutCardIn(this.id,specifiedZone,parameters,actionBuilder));
      case SecurityEventName.ROOM_EXIT ->
        actionsToSend.addAll(recordRoomExitIn(specifiedZone,parameters,actionBuilder));
      case SecurityEventName.GATHERING_STARTED ->
        actionsToSend.addAll(registerGatheringStarted(specifiedZone,parameters,actionBuilder));
      case SecurityEventName.GATHERING_ENDED ->
        actionsToSend.addAll(registerGatheringEnded(specifiedZone,parameters,actionBuilder));
    }
    return actionsToSend;
  }

  public List<Action> evaluatePrealarmSituationIn(Zone specifiedZone,ActionBuilder actionBuilder,
      UserRepository userRepository){

    if (zoneHasLargeGroup()){
      return triggerFireEmergencyIn(specifiedZone,actionBuilder,userRepository);
    }

    return triggerFirePrealarmIn(specifiedZone,actionBuilder);
  }

  public List<Action> triggerFireEmergencyIn(Zone specifiedZone,ActionBuilder actionBuilder,
      UserRepository userRepository){
    List<Action> actionsToSend = new ArrayList<>();
    for (Zone zone : this.zones){
      if (zone != specifiedZone){
        actionsToSend.addAll(zone.triggerFireEmergencyInOtherZones(this.id,actionBuilder));
      }
    }

    if (specifiedZone != null){
      actionsToSend.addAll(askAssistance(actionBuilder));
      actionsToSend
          .addAll(specifiedZone.triggerFireEmergency(this.id,actionBuilder,userRepository));
      actionsToSend.addAll(adjustDoorsForFire(actionBuilder));
      actionsToSend.addAll(activateAllFireAlarms(actionBuilder));
    }
    return actionsToSend;
  }

  public List<Action> triggerFireExtinguishedIn(Zone specifiedZone,ActionBuilder actionBuilder,
      UserRepository userRepository){
    List<Action> actionsToSend = new ArrayList<>();

    if (specifiedZone != null){
      actionsToSend
          .addAll(specifiedZone.resolveFireExtinguished(this.id,actionBuilder,userRepository));
      actionsToSend.addAll(adjustVentilationInZoneForFireExtinguished(specifiedZone,actionBuilder));
      actionsToSend.addAll(adjustDoorsWhenFireExtinguished(actionBuilder));
      actionsToSend.addAll(deactivateAllFireAlarms(actionBuilder));
    }
    return actionsToSend;
  }

  private void registerAgentDeploymentIn(Zone specifiedZone,List<EventParameter> parameters){
    InterventionId interventionId = this.parameterReader.extractInterventionId(parameters);

    specifiedZone.registerAgentDeployment(interventionId);
  }

  public List<Action> triggerFirePrealarmIn(Zone specifiedZone,ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();

    if (specifiedZone != null){
      actionsToSend.addAll(specifiedZone.triggerFirePrealarm(this.id,actionBuilder));
    }
    return actionsToSend;
  }

  public List<Action> processSmokeLevelsIn(Zone specifiedZone,List<EventParameter> parameters,
      ActionBuilder actionBuilder,UserRepository userRepository){
    List<Action> actionsToSend = new ArrayList<>();
    int concentration = this.parameterReader.extractConcentration(parameters);

    if (this.smokeThreshold.isAtThreshold1(concentration)){
      actionsToSend.addAll(triggerFirePrealarmIn(specifiedZone,actionBuilder));
    } else if (this.smokeThreshold.isAtOrAboveThreshold2(concentration)){
      actionsToSend.addAll(triggerFireEmergencyIn(specifiedZone,actionBuilder,userRepository));
    }
    setSmokeConcentrationInZone(specifiedZone,concentration);
    return actionsToSend;
  }

  public List<Action> reportEntryWithoutCardIn(BuildingId buildingId,Zone specifiedZone,
      List<EventParameter> parameters,ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    RoomId roomId = this.parameterReader.extractRoomIdFromParameter(parameters);

    if (specifiedZone != null){
      actionsToSend.addAll(specifiedZone.registerEntryWithoutCard(buildingId,roomId,actionBuilder));
    }

    return actionsToSend;
  }

  public List<Action> recordRoomExitIn(Zone specifiedZone,List<EventParameter> parameters,
      ActionBuilder actionBuilder){
    RoomId roomId = this.parameterReader.extractRoomIdFromParameter(parameters);

    if (specifiedZone != null){
      return specifiedZone.registerRoomExit(roomId,this.id,actionBuilder);
    }
    return new ArrayList<>();
  }

  public AccessStatus evaluateAccessTo(AccessCard accessCard,ZoneId zoneId,DoorId doorId,
      RoomId roomId){
    Optional<Zone> fetchedZone = getZoneInBuilding(zoneId);
    if (fetchedZone.isEmpty() || (accessCard != null && accessCard.userDoesNotExist())
        || accessCard == null){
      return AccessStatus.DENIED;
    }
    Zone zone = fetchedZone.get();

    AccessContext context = new AccessContext(accessCard);

    addContextForAccessRules(context,accessCard,zone);

    zone.addContextForAccessRules(accessCard,context,roomId,doorId);

    AccessStatus status = this.accessEvaluator.evaluate(context);
    if (status == AccessStatus.ALLOWED){
      zone.registerRoomEntry(roomId,accessCard);
    }

    return status;
  }

  private Optional<Zone> getZoneInBuilding(ZoneId zoneId){
    return this.zones.stream().filter(zone -> zoneId.equals(zone.getId())).findFirst();
  }

  private void addContextForAccessRules(AccessContext context,AccessCard accessCard,Zone zone){
    context.setBuildingHasAnyZoneOnFire(hasAnyZoneOnFire());
    context.setUserIsALabResponsibleInThisBuilding(checkIfUserIsLabResponsible(accessCard));

    context.setAccessDeniedByExtremeNegativePressure(
        zone.evaluateAccessInCaseOfExtremeNegativePressure(accessCard) == AccessStatus.DENIED);
  }

  private boolean hasAnyZoneOnFire(){
    return this.zones.stream().anyMatch(Zone::isOnFire);
  }

  private boolean checkIfUserIsLabResponsible(AccessCard accessCard){
    if (accessCard == null){
      return false;
    }

    for (Zone zone : zones){
      if (zone.checkIfUserIsLabResponsibleForAnyRoom(accessCard.idul())){
        return true;
      }
    }
    return false;
  }

  public List<Action> registerGatheringStarted(Zone specifiedZone,List<EventParameter> parameters,
      ActionBuilder actionBuilder){
    GatheringId gatheringId = this.parameterReader.extractGatheringId(parameters);
    int expectedAttendees = this.parameterReader.extractExpectedAttendees(parameters);
    GatheringType gatheringType = this.parameterReader.extractGatheringType(parameters);
    Gathering gathering = GatheringFactory.createGathering(gatheringId,expectedAttendees,
        gatheringType);
    return specifiedZone.registerGatheringStarted(gathering,this.id,actionBuilder);
  }

  public List<Action> registerGatheringEnded(Zone specifiedZone,List<EventParameter> parameters,
      ActionBuilder actionBuilder){
    GatheringId gatheringId = this.parameterReader.extractGatheringId(parameters);
    return specifiedZone.registerGatheringEnded(gatheringId,this.id,actionBuilder);
  }

  private void setSmokeConcentrationInZone(Zone specifiedZone,int concentration){
    specifiedZone.setSmokeConcentrationLevel(concentration);
  }

  private List<Action> adjustDoorsForFire(ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();

    for (Zone zone : this.zones){
      actionsToSend.addAll(zone.applyRulesForDoorsDuringFire(actionBuilder,this.id));
    }
    return actionsToSend;
  }

  private List<Action> adjustDoorsWhenFireExtinguished(ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();

    for (Zone zone : this.zones){
      actionsToSend.addAll(zone.applyRulesForDoorsWhenExtinguished(actionBuilder,this.id));
    }

    return actionsToSend;
  }

  private List<Action> adjustVentilationInZoneForFireExtinguished(Zone specifiedZone,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();

    boolean otherFireExists = hasAnyZoneOnFire();

    if (!otherFireExists){
      for (Zone zone : this.zones){
        actionsToSend.addAll(zone.resolveVentilationForFireExtinguished(this.id,actionBuilder));
      }
    } else{
      actionsToSend.addAll(specifiedZone.triggerFireEmergencyInOtherZones(this.id,actionBuilder));
    }

    return actionsToSend;
  }

  private List<Action> activateAllFireAlarms(ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    for (Zone zone : this.zones){
      actionsToSend.addAll(zone.activateFireAlarms(this.id,actionBuilder));
    }
    return actionsToSend;
  }

  private List<Action> askAssistance(ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    if (!hasAnyZoneOnFire()){
      actionsToSend.add(
          actionBuilder.createCallFireFighterAction(this.postalAddress,FireFighterCallReason.FIRE));
    }
    return actionsToSend;
  }

  private List<Action> deactivateAllFireAlarms(ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    if (!hasAnyZoneOnFire()){
      for (Zone zone : this.zones){
        actionsToSend.addAll(zone.deactivateFireAlarms(this.id,actionBuilder));
      }
    }
    return actionsToSend;
  }

  private List<Action> registerAgentArrivalIn(Zone specifiedZone,List<EventParameter> parameters,
      ActionBuilder actionBuilder){

    InterventionId interventionId = this.parameterReader.extractInterventionId(parameters);
    return new ArrayList<>(
        specifiedZone.registerAgentArrivedAtIntervention(interventionId,actionBuilder,this.id));
  }

  private void registerAgentLeavingIn(Zone specifiedZone,List<EventParameter> parameters){
    InterventionId interventionId = this.parameterReader.extractInterventionId(parameters);
    specifiedZone.registerAgentLeftIntervention(interventionId);
  }

  private boolean zoneHasLargeGroup(){
    return zones.stream().anyMatch(Zone::hasLargeGroup);
  }
}
