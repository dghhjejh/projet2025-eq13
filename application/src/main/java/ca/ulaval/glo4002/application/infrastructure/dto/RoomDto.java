package ca.ulaval.glo4002.application.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RoomDto{
  @JsonProperty("id")
  public String id;

  @JsonProperty("name")
  public String name;

  @JsonProperty("type")
  public String type;

  @JsonProperty("supportsElectricClosure")
  public boolean supportsElectricClosure;

  @JsonProperty("labSafetyResponsible")
  public String labSafetyResponsible;
}
