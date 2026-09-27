package ca.ulaval.glo4002.application.interfaces.rest.mappers.parameters;

import ca.ulaval.glo4002.application.domain.actions.CallFireFighter;
import java.util.Map;

public class CallFireFighterParameters{
  public static Map<String, String> toParameters(CallFireFighter action){
    return Map.of("adresseBatiment",action.adresse().toString(),"raison",
        String.valueOf(action.reason()));
  }
}
