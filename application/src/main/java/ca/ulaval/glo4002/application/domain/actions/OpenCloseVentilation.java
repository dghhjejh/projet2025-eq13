package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Objects;

public record OpenCloseVentilation(BuildingId buildingId, ZoneId zoneId,
    int isOpen) implements Action {
  private static final ActionName ACTION_NAME = ActionName.OpenCloseVentilation;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof OpenCloseVentilation that))
      return false;
    return this.isOpen == that.isOpen && this.buildingId.equals(that.buildingId)
        && this.zoneId.equals(that.zoneId);
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.buildingId,this.zoneId,this.isOpen);
  }
}
