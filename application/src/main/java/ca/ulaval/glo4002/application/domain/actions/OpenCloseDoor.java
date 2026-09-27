package ca.ulaval.glo4002.application.domain.actions;

import static ca.ulaval.glo4002.application.domain.actions.enums.ActionName.OpenCloseDoor;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Objects;

public record OpenCloseDoor(BuildingId buildingId, ZoneId zoneId, DoorId doorId,
    boolean isOpen) implements Action {
  private static final ActionName ACTION_NAME = OpenCloseDoor;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof OpenCloseDoor that))
      return false;
    return this.isOpen == that.isOpen && this.buildingId.equals(that.buildingId)
        && this.zoneId.equals(that.zoneId) && this.doorId.equals(that.doorId);
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.buildingId,this.zoneId,this.doorId,this.isOpen);
  }
}
