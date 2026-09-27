package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.SendPredefinedTeams;
import java.util.Map;

public class SendPredefinedTeamsParameters{
  public static Map<String, String> toParameters(SendPredefinedTeams action){
    return Map.of("idDestination",action.responsibleId().toString(),"codeMessage",
        action.message().toString());
  }
}
