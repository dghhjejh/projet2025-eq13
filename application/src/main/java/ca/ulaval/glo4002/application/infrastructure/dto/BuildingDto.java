package ca.ulaval.glo4002.application.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingDto{
  @JsonProperty("id")
  public String id;

  @JsonProperty("name")
  public String name;

  @JsonProperty("postalAddress")
  public String postalAddress;

  @JsonProperty("ventilationSystem")
  public String ventilationSystemType;

  @JsonProperty("zones")
  public List<ZoneDto> zones;
}
