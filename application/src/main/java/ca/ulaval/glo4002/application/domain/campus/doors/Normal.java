package ca.ulaval.glo4002.application.domain.campus.doors;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.List;

public class Normal extends Door{

  public Normal(DoorId doorId,Boolean onEvacuationPath,Boolean isLocked,Boolean isOpen,
      Boolean initalLockedState,Boolean initialOpenState) {
    super(doorId, DoorType.NORMAL, onEvacuationPath, isLocked, isOpen, initalLockedState,
        initialOpenState);
  }

  @Override
  public List<Action> triggerFireEmergency(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    return new ArrayList<>();
  }

  @Override
  public List<Action> resolveFireExtinguished(ActionBuilder actionBuilder,BuildingId buildingId,
      ZoneId zoneId){
    return new ArrayList<>();
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
    accessContext.setAccessRequestedForBuildingAccess(false);
  }
}
