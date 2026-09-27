package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Objects;

public record AdjustVentilationSpeed(BuildingId buildingId, ZoneId zoneId, double distributionSpeed,
    double returnSpeed) implements Action {
  private static final ActionName ACTION_NAME = ActionName.AdjustVentilationSpeed;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof AdjustVentilationSpeed that))
      return false;
    return (Double.compare(that.distributionSpeed,this.distributionSpeed) == 0)
        && (Double.compare(that.returnSpeed,this.returnSpeed) == 0)
        && this.buildingId.equals(that.buildingId) && this.zoneId.equals(that.zoneId);
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.distributionSpeed,this.returnSpeed,this.buildingId,this.zoneId);
  }
}
