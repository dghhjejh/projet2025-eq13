package ca.ulaval.glo4002.application.domain.campus.zones;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationState;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationParameters;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import java.util.*;

public class HazardousMaterialZone extends Zone{
  private static final int DOOR_THRESHOLD_FOR_VENTILATION = 5;

  public HazardousMaterialZone(ZoneId zoneId,ZoneType type,boolean currentlyHasActivity,
      List<Door> doors,List<Room> rooms,UrgencyState urgencyState,int smokeConcentration,
      boolean hasActiveAlarms,VentilationSystem ventilationSystem,
      List<RequestAgent> agentsRequested,List<Agent> agentsDeployedInZone,
      List<Agent> agentsArrivedInZone,List<Gathering> gatheringsInZone) {
    super(zoneId, type, currentlyHasActivity, doors, rooms, urgencyState, smokeConcentration,
        hasActiveAlarms, ventilationSystem, agentsRequested, agentsDeployedInZone,
        agentsArrivedInZone, gatheringsInZone);
  }

  @Override
  public List<Action> triggerFireEmergency(BuildingId buildingId,ActionBuilder actionBuilder,
      UserRepository userRepository){
    List<Action> actionsToSend = new ArrayList<>();
    Set<Idul> notifiedResponsibles = new HashSet<>();
    setUrgencyState(UrgencyState.FIRE);
    VentilationParameters ventilationParameters = determineVentilationParameters(false);
    for (Room room : getRooms()){
      actionsToSend.addAll(room.triggerFireEmergency(buildingId,getId(),actionBuilder,
          userRepository,notifiedResponsibles));
    }
    actionsToSend
        .addAll(sendAgents(actionBuilder,getNumberOfAgentsToSendForFire(),PRIORITY_FOR_FIRES));
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        ventilationParameters.medianSpeed(),ventilationParameters.pressureVariation(),
        ventilationParameters.isOpen(),actionBuilder));

    return actionsToSend;
  }

  @Override
  public List<Action> triggerFireEmergencyInOtherZones(BuildingId buildingId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    VentilationParameters ventilationParameters = determineVentilationParameters(true);
    SimpleVentilationState isOpen = SimpleVentilationState.OPEN;
    if (!isOnFire()){
      actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
          ventilationParameters.medianSpeed(),ventilationParameters.pressureVariation(),isOpen,
          actionBuilder));
    }
    return actionsToSend;
  }

  @Override
  public List<Action> triggerFirePrealarm(BuildingId buildingId,ActionBuilder actionBuilder){
    setUrgencyState(UrgencyState.PREALARM);
    VentilationParameters ventilationParameters = determineVentilationParameters(false);
    SimpleVentilationState isOpen = SimpleVentilationState.CLOSED;
    List<Action> actionsToSend = new ArrayList<>(
        sendAgents(actionBuilder,getNumberOfAgentsToSendForFirePrealarm(),PRIORITY_FOR_FIRES));
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        ventilationParameters.medianSpeed(),ventilationParameters.pressureVariation(),isOpen,
        actionBuilder));
    return actionsToSend;
  }

  @Override
  public List<Action> resolveVentilationForFireExtinguished(BuildingId buildingId,
      ActionBuilder actionBuilder){
    setUrgencyState(UrgencyState.NORMAL);
    List<Action> actionsToSend = new ArrayList<>();
    VentilationParameters ventilationParameters = determineVentilationParameters(false);
    SimpleVentilationState isOpen = SimpleVentilationState.OPEN;
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        ventilationParameters.medianSpeed(),ventilationParameters.pressureVariation(),isOpen,
        actionBuilder));
    return actionsToSend;
  }

  @Override
  public List<Action> registerRoomExit(RoomId roomId,BuildingId buildingId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();
    getRoomInZone(roomId).ifPresent(Room::decrementOccupation);
    VentilationParameters ventilationParameters = determineVentilationParameters(false);
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        ventilationParameters.medianSpeed(),ventilationParameters.pressureVariation(),
        ventilationParameters.isOpen(),actionBuilder));
    return actionsToSend;
  }

  @Override
  public List<Action> registerEntryWithoutCard(BuildingId buildingId,RoomId roomId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>(
        getRoomInZone(roomId).map(room -> room.registerEntryWithoutCard(buildingId,actionBuilder,
            getId(),() -> setAreAlarmsActive(true))).orElseGet(List::of));
    VentilationParameters ventilationParameters = determineVentilationParameters(false);
    actionsToSend.add(getVentilationSystem().adjustVentilation(buildingId,getId(),
        ventilationParameters.medianSpeed(),ventilationParameters.pressureVariation(),
        ventilationParameters.isOpen(),actionBuilder));
    return actionsToSend;
  }

  private VentilationParameters determineVentilationParameters(boolean otherZone){
    if (getUrgencyState() == UrgencyState.PREALARM || getUrgencyState() == UrgencyState.FIRE){
      if (getTotalOccupation() > 0){
        return new VentilationParameters(70,-30,SimpleVentilationState.CLOSED);
      } else if (getTotalOccupation() == 0 && getDoors().size() >= DOOR_THRESHOLD_FOR_VENTILATION){
        return new VentilationParameters(50,-50,SimpleVentilationState.CLOSED);
      } else if (getTotalOccupation() == 0 && getDoors().size() < DOOR_THRESHOLD_FOR_VENTILATION){
        return new VentilationParameters(50,-40,SimpleVentilationState.CLOSED);
      }
    }
    if (otherZone){
      return new VentilationParameters(75,25,SimpleVentilationState.OPEN);
    }
    return new VentilationParameters(50,-10,SimpleVentilationState.OPEN);
  }
}
