package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.SendPredefinedSMS;
import java.util.Map;

public class SendPredefinedSMSParameters{
  public static Map<String, String> toParameters(SendPredefinedSMS action){
    return Map.of("numeroDestination",action.phoneNumber().toString(),"nomDestination",
        action.name(),"codeMessage",action.message().toString());
  }
}
