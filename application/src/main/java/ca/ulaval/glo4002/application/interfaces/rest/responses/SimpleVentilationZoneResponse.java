package ca.ulaval.glo4002.application.interfaces.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class SimpleVentilationZoneResponse extends ZoneResponse{
  private final String ventilation;

  public SimpleVentilationZoneResponse(String id,String etat_incendie,boolean présence_de_fumée,
      String ventilation,int occupation_actuelle,List<String> occupants_identifiés,
      Map<String, Integer> acces_consecutifs_par_personne) {
    super(id, etat_incendie, présence_de_fumée, occupation_actuelle, occupants_identifiés,
        acces_consecutifs_par_personne);
    this.ventilation = ventilation;
  }

  @JsonProperty("ventilation")
  public String getVentilation(){
    return ventilation;
  }
}
