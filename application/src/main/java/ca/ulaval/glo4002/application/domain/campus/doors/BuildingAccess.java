package ca.ulaval.glo4002.application.domain.campus.doors;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class BuildingAccess extends Door{

  public BuildingAccess(DoorId doorId,Boolean onEvacuationPath,Boolean isLocked,Boolean isOpen,
      Boolean initialLockedState,Boolean initialOpenState) {
    super(doorId, DoorType.BUILDING_ACCESS, onEvacuationPath, isLocked, isOpen, initialLockedState,
        initialOpenState);
  }

  @Override
  public List<Action> triggerFireEmergency(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    List<Action> actionsToSend = new ArrayList<>();
    boolean shouldBeLocked = true;
    boolean shouldBeOpen = false;
    if (isLocked != shouldBeLocked){
      actionsToSend
          .add(actionBuilder.createLockUnlockDoorAction(buildingId,zoneId,id,shouldBeLocked));
      isLocked = shouldBeLocked;
    }
    if (isOpen != shouldBeOpen){
      actionsToSend.add(actionBuilder.createOpenCloseDoorAction(buildingId,zoneId,id,shouldBeOpen));
      isOpen = shouldBeOpen;
    }
    return actionsToSend;
  }

  @Override
  public List<Action> resolveFireExtinguished(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    List<Action> actionsToSend = new ArrayList<>();
    if (initialLockedState != isLocked){
      actionsToSend
          .add(actionBuilder.createLockUnlockDoorAction(buildingId,zoneId,id,initialLockedState));
      isLocked = initialLockedState;
    }
    if (initialOpenState != isOpen){
      actionsToSend
          .add(actionBuilder.createOpenCloseDoorAction(buildingId,zoneId,id,initialOpenState));
      isOpen = initialOpenState;
    }
    return actionsToSend;
  }

  @Override
  public List<Action> applyGatheringStarted(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    return new ArrayList<>();
  }

  @Override
  public List<Action> applyGatheringEnded(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    return new ArrayList<>();
  }

  @Override
  public void addContextForAccessRules(AccessContext accessContext){
    accessContext.setAccessRequestedForBuildingAccess(true);
  }
}
