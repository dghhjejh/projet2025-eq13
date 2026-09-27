package ca.ulaval.glo4002.application.domain.campus.doors;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.List;

public abstract class Door{
  protected DoorId id;

  protected DoorType type;

  protected Boolean onEvacuationPath;

  protected Boolean isLocked;

  protected Boolean initialLockedState;

  protected Boolean isOpen;

  protected Boolean initialOpenState;

  protected Door() {
  }

  protected Door(DoorId doorId,DoorType type,boolean onEvacuationPath,boolean isLocked,
      boolean isOpen,boolean initialLockedState,boolean initialOpenState) {
    this.id = doorId;
    this.type = type;
    this.onEvacuationPath = onEvacuationPath;
    this.initialLockedState = initialLockedState;
    this.initialOpenState = initialOpenState;
    this.isLocked = isLocked;
    this.isOpen = isOpen;
  }

  public DoorId getId(){
    return this.id;
  }

  public void setId(DoorId id){
    this.id = id;
  }

  public DoorType getType(){
    return this.type;
  }

  public void setType(DoorType type){
    this.type = type;
  }

  public Boolean getIsLocked(){
    return this.isLocked;
  }

  public void setIsLocked(boolean isLocked){
    this.isLocked = isLocked;

    if (this.initialLockedState == null){
      this.initialLockedState = isLocked;
    }
  }

  public Boolean getIsOpen(){
    return this.isOpen;
  }

  public void setIsOpen(boolean isOpen){
    this.isOpen = isOpen;

    if (this.initialOpenState == null){
      this.initialOpenState = isOpen;
    }
  }

  public Boolean getInitialLockedState(){
    return this.initialLockedState;
  }

  public Boolean getInitialOpenState(){
    return this.initialOpenState;
  }

  public Boolean getOnEvacuationPath(){
    return this.onEvacuationPath;
  }

  public abstract List<Action> triggerFireEmergency(ActionBuilder actionBuilder,
      BuildingId buildingId,ZoneId zoneId);

  public abstract List<Action> resolveFireExtinguished(ActionBuilder actionBuilder,
      BuildingId buildingId,ZoneId zoneId);

  public abstract List<Action> applyGatheringStarted(ActionBuilder actionBuilder,
      BuildingId buildingId,ZoneId zoneId);

  public abstract List<Action> applyGatheringEnded(ActionBuilder actionBuilder,
      BuildingId buildingId,ZoneId zoneId);

  public abstract void addContextForAccessRules(AccessContext accessContext);

  protected enum DoorCondition{
    FIRE_EMERGENCY, GATHERING_IN_PROGRESS
  }
}
