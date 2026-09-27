package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.actions.enums.FireFighterCallReason;
import ca.ulaval.glo4002.application.domain.campus.buildings.PostalAddress;
import java.util.Objects;

public record CallFireFighter(PostalAddress adresse,
    FireFighterCallReason reason) implements Action {
  private static final ActionName ACTION_NAME = ActionName.CallFireFighter;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof CallFireFighter that))
      return false;
    return this.adresse.equals(that.adresse) && this.reason.equals(that.reason);
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.adresse,this.reason);
  }
}
