package ca.ulaval.glo4002.application.domain.campus.zones;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationState;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import java.util.*;

public class NormalZone extends Zone{

  public NormalZone(ZoneId zoneId,ZoneType type,boolean currentlyHasActivity,List<Door> doors,
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
    List<Action> actionsToSend = new ArrayList<>();
    Set<Idul> notifiedResponsibles = new HashSet<>();
    int medianVentilationSpeed = 75;
    int pressureVariation = -25;
    SimpleVentilationState isOpen = SimpleVentilationState.CLOSED;
    for (Room room : getRooms()){
      actionsToSend.addAll(room.triggerFireEmergency(buildingId,getId(),actionBuilder,
          userRepository,notifiedResponsibles));
    }
    actionsToSend
        .addAll(sendAgents(actionBuilder,getNumberOfAgentsToSendForFire(),PRIORITY_FOR_FIRES));
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        medianVentilationSpeed,pressureVariation,isOpen,actionBuilder));

    setUrgencyState(UrgencyState.FIRE);
    return actionsToSend;
  }

  @Override
  public List<Action> triggerFireEmergencyInOtherZones(BuildingId buildingId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    int medianVentilationSpeed = 75;
    int pressureVariation = 25;
    SimpleVentilationState isOpen = SimpleVentilationState.OPEN;
    if (!isOnFire()){
      actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
          medianVentilationSpeed,pressureVariation,isOpen,actionBuilder));
    }
    return actionsToSend;
  }

  @Override
  public List<Action> triggerFirePrealarm(BuildingId buildingId,ActionBuilder actionBuilder){
    setUrgencyState(UrgencyState.PREALARM);
    int medianVentilationSpeed = 75;
    int pressureVariation = -25;
    SimpleVentilationState isOpen = SimpleVentilationState.CLOSED;
    List<Action> actionsToSend = new ArrayList<>(
        sendAgents(actionBuilder,getNumberOfAgentsToSendForFirePrealarm(),PRIORITY_FOR_FIRES));
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        medianVentilationSpeed,pressureVariation,isOpen,actionBuilder));
    return actionsToSend;
  }

  @Override
  public List<Action> resolveVentilationForFireExtinguished(BuildingId buildingId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    int medianVentilationSpeed = 50;
    int pressureVariation = 10;
    SimpleVentilationState isOpen = SimpleVentilationState.OPEN;
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        medianVentilationSpeed,pressureVariation,isOpen,actionBuilder));
    return actionsToSend;
  }
}
