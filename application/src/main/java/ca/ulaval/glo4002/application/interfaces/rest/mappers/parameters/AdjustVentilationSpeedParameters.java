package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.AdjustVentilationSpeed;
import java.util.Map;

public class AdjustVentilationSpeedParameters{
  public static Map<String, String> toParameters(AdjustVentilationSpeed action){
    return Map.of("batiment",action.buildingId().toString(),"zone",action.zoneId().toString(),
        "distribution",String.valueOf(action.distributionSpeed()),"retour",
        String.valueOf(action.returnSpeed()));
  }
}
