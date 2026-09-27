package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.LockUnlockDoor;
import java.util.Map;

public class LockUnlockDoorParameters{
  public static Map<String, String> toParameters(LockUnlockDoor action){
    return Map.of("batiment",action.buildingId().toString(),"zone",action.zoneId().toString(),
        "porte",String.valueOf(action.doorId()),"valeur",action.isLocked() ? "1" : "0");
  }
}
