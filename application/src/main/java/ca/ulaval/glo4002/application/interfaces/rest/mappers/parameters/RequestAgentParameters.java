package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import java.util.Map;

public class RequestAgentParameters{

  public static final String agentType = "PATROUILLEUR";

  public static Map<String, String> toParameters(RequestAgent action){
    return Map.of("typeAgent",agentType,"idAgent",
        action.agentId() != null ? action.agentId().toString() : "null","zone",
        action.zoneId().toString(),"priorite",action.priority().toString(),"idIntervention",
        action.interventionId().toString());
  }
}
