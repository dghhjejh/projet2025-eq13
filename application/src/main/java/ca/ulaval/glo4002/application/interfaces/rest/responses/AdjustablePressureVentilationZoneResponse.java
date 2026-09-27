package ca.ulaval.glo4002.application.interfaces.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public class AdjustablePressureVentilationZoneResponse extends ZoneResponse{
  private final Double ventilation_vitesse_distribution;
  private final Double ventilation_vitesse_retour;

  public AdjustablePressureVentilationZoneResponse(String id,String etat_incendie,
      boolean présence_de_fumée,Double ventilation_vitesse_distribution,
      Double ventilation_vitesse_retour,int occupation_actuelle,List<String> occupants_identifiés,
      Map<String, Integer> acces_consecutifs_par_personne) {
    super(id, etat_incendie, présence_de_fumée, occupation_actuelle, occupants_identifiés,
        acces_consecutifs_par_personne);
    this.ventilation_vitesse_distribution = ventilation_vitesse_distribution;
    this.ventilation_vitesse_retour = ventilation_vitesse_retour;
  }

  @JsonProperty("ventilation_vitesse_distribution")
  public Double getVentilationDistributionSpeed(){
    return ventilation_vitesse_distribution;
  }

  @JsonProperty("ventilation_vitesse_retour")
  public Double getVentilationReturnSpeed(){
    return ventilation_vitesse_retour;
  }
}
