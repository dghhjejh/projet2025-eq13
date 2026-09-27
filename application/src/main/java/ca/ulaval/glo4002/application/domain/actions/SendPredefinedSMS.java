package ca.ulaval.glo4002.application.domain.actions;

import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.bottin.PhoneNumber;
import ca.ulaval.glo4002.application.domain.bottin.PredefinedMessages;

public record SendPredefinedSMS(PhoneNumber phoneNumber, String name,
    PredefinedMessages message) implements Action {
  private static final ActionName ACTION_NAME = ActionName.SendPredefinedSMS;

  @Override
  public ActionName getName(){
    return ACTION_NAME;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof SendPredefinedSMS that))
      return false;
    return this.phoneNumber.equals(that.phoneNumber) && this.name.equals(that.name)
        && this.message.equals(that.message);
  }

  @Override
  public int hashCode(){
    return java.util.Objects.hash(this.phoneNumber,this.name,this.message);
  }
}
