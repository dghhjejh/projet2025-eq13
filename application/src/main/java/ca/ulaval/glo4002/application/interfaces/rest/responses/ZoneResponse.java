package ca.ulaval.glo4002.application.interfaces.rest.responses;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import java.util.List;
import java.util.Map;

@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({@JsonSubTypes.Type(AdjustablePressureVentilationZoneResponse.class),
    @JsonSubTypes.Type(SimpleVentilationZoneResponse.class)})
public abstract class ZoneResponse{
  protected final String id;
  protected final String etat_incendie;
  protected final boolean présence_de_fumée;
  protected final int occupation_actuelle;
  protected final List<String> occupants_identifiés;
  protected final Map<String, Integer> acces_consecutifs_par_personne;

  protected ZoneResponse(String id,String etat_incendie,boolean présence_de_fumée,
      int occupation_actuelle,List<String> occupants_identifiés,
      Map<String, Integer> acces_consecutifs_par_personne) {
    this.id = id;
    this.etat_incendie = etat_incendie;
    this.présence_de_fumée = présence_de_fumée;
    this.occupation_actuelle = occupation_actuelle;
    this.occupants_identifiés = occupants_identifiés;
    this.acces_consecutifs_par_personne = acces_consecutifs_par_personne;
  }

  @JsonProperty("id")
  public String getId(){
    return id;
  }

  @JsonProperty("etat_incendie")
  public String getFireState(){
    return etat_incendie;
  }

  @JsonProperty("présence_de_fumée")
  public boolean getSmokePresence(){
    return présence_de_fumée;
  }

  @JsonProperty("occupation_actuelle")
  public int getOccupationActuelle(){
    return occupation_actuelle;
  }

  @JsonProperty("occupants_identifiés")
  public List<String> getOccupantsIdentifies(){
    return occupants_identifiés;
  }

  @JsonProperty("acces_consecutifs_par_personne")
  public Map<String, Integer> getAccesConsecutifsParPersonne(){
    return acces_consecutifs_par_personne;
  }
}
