package ca.ulaval.glo4002.application.domain.campus.doors;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

public class FireDoor extends Door{
  private final Deque<DoorCondition> doorConditions = new ArrayDeque<>();

  public FireDoor(DoorId doorId,Boolean onEvacuationPath,Boolean isLocked,Boolean isOpen,
      Boolean initialLockedState,Boolean initialOpenState) {
    super(doorId, DoorType.FIRE_DOOR, onEvacuationPath, isLocked, isOpen, initialLockedState,
        initialOpenState);
  }

  @Override
  public List<Action> triggerFireEmergency(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    List<Action> actionsToSend = new ArrayList<>();
    if (!doorConditions.contains(DoorCondition.FIRE_EMERGENCY)){
      doorConditions.push(DoorCondition.FIRE_EMERGENCY);
    }
    if (this.isOpen){
      this.isOpen = false;
      actionsToSend.add(actionBuilder.createOpenCloseDoorAction(buildingId,zoneId,this.id,false));
    }

    if (this.onEvacuationPath && this.isLocked){
      this.isLocked = false;
      actionsToSend.add(actionBuilder.createLockUnlockDoorAction(buildingId,zoneId,this.id,false));
    }

    return actionsToSend;
  }

  @Override
  public List<Action> resolveFireExtinguished(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    doorConditions.remove(DoorCondition.FIRE_EMERGENCY);
    return restoreToAppropriateState(actionBuilder,buildingId,zoneId);
  }

  @Override
  public List<Action> applyGatheringStarted(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    List<Action> actions = new ArrayList<>();

    if (!doorConditions.contains(DoorCondition.GATHERING_IN_PROGRESS)){
      doorConditions.addLast(DoorCondition.GATHERING_IN_PROGRESS);
    }

    if (doorConditions.contains(DoorCondition.FIRE_EMERGENCY)){
      return actions;
    }

    if (this.isOpen){
      this.isOpen = false;
    }

    if (!this.isLocked){
      this.isLocked = true;
      actions.add(actionBuilder.createLockUnlockDoorAction(buildingId,zoneId,this.id,true));
    }

    return actions;
  }

  @Override
  public List<Action> applyGatheringEnded(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){

    doorConditions.remove(DoorCondition.GATHERING_IN_PROGRESS);

    if (doorConditions.contains(DoorCondition.FIRE_EMERGENCY)){
      return new ArrayList<>();
    }

    return restoreToAppropriateState(actionBuilder,buildingId,zoneId);
  }

  private List<Action> restoreToAppropriateState(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    List<Action> actionsToSend = new ArrayList<>();

    if (!this.isOpen.equals(this.initialOpenState)){
      this.isOpen = this.initialOpenState;
      actionsToSend.add(
          actionBuilder.createOpenCloseDoorAction(buildingId,zoneId,this.id,this.initialOpenState));
    }

    if (!this.isLocked.equals(this.initialLockedState)){
      this.isLocked = this.initialLockedState;
      actionsToSend.add(actionBuilder.createLockUnlockDoorAction(buildingId,zoneId,this.id,
          this.initialLockedState));
    }

    return actionsToSend;
  }

  @Override
  public void addContextForAccessRules(AccessContext accessContext){
    accessContext.setAccessRequestedForBuildingAccess(false);
  }
}
