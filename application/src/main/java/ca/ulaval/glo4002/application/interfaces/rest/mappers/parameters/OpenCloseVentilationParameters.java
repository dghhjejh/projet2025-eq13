package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.OpenCloseVentilation;
import java.util.Map;

public class OpenCloseVentilationParameters{
  public static Map<String, String> toParameters(OpenCloseVentilation action){
    return Map.of("batiment",action.buildingId().toString(),"zone",action.zoneId().toString(),
        "valeur",String.valueOf(action.isOpen()));
  }
}
