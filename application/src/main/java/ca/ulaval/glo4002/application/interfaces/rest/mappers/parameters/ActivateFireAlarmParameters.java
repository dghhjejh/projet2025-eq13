package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.ActivateFireAlarm;
import java.util.Map;

public class ActivateFireAlarmParameters{
  public static Map<String, String> toParameters(ActivateFireAlarm action){
    return Map.of("batiment",action.buildingId().toString(),"zone",action.zoneId().toString(),
        "sonnerie",action.shouldActivate() ? "true" : "false");
  }
}
