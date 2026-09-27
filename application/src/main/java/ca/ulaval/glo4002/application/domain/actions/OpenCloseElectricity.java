package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Objects;

public record OpenCloseElectricity(BuildingId buildingId, ZoneId zoneId, RoomId localId,
    boolean close) implements Action {

  private static final ActionName ACTION_NAME = ActionName.OpenCloseElectricity;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof OpenCloseElectricity that))
      return false;
    return this.close == that.close && this.buildingId.equals(that.buildingId)
        && this.zoneId.equals(that.zoneId) && this.localId.equals(that.localId);
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.buildingId,this.zoneId,this.localId,this.close);
  }
}
