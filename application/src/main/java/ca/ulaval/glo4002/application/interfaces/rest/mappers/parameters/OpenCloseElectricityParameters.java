package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.OpenCloseElectricity;
import java.util.Map;

public class OpenCloseElectricityParameters{
  public static Map<String, String> toParameters(OpenCloseElectricity action){
    return Map.of("batiment",action.buildingId().toString(),"zone",action.zoneId().toString(),
        "local",String.valueOf(action.localId()),"valeur",action.close() ? "0" : "1");
  }
}
