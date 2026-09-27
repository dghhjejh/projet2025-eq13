package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.PredefinedMessages;

public record SendPredefinedTeams(Idul responsibleId,
    PredefinedMessages message) implements Action {
  private static final ActionName ACTION_NAME = ActionName.SendPredefinedTeams;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof SendPredefinedTeams that))
      return false;
    return this.responsibleId.equals(that.responsibleId) && this.message.equals(that.message);
  }

  @Override
  public int hashCode(){
    return java.util.Objects.hash(this.responsibleId,this.message);
  }
}
