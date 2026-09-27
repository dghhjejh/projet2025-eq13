package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.OpenCloseDoor;
import java.util.Map;

public class OpenCloseDoorParameters{
  public static Map<String, String> toParameters(OpenCloseDoor action){
    return Map.of("batiment",action.buildingId().toString(),"zone",action.zoneId().toString(),
        "porte",action.doorId().toString(),"valeur",action.isOpen() ? "1" : "0");
  }
}
