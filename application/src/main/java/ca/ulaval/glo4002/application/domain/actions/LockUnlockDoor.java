package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Objects;

public record LockUnlockDoor(BuildingId buildingId, ZoneId zoneId, DoorId doorId,
    Boolean isLocked) implements Action {
  private static final ActionName ACTION_NAME = ActionName.LockUnlockDoor;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof LockUnlockDoor that))
      return false;
    return this.isLocked.equals(that.isLocked) && this.buildingId.equals(that.buildingId)
        && this.zoneId.equals(that.zoneId) && this.doorId.equals(that.doorId);
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.buildingId,this.zoneId,this.doorId,this.isLocked);
  }
}
