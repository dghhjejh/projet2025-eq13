package ca.ulaval.glo4002.application.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BuildingMapDto{
  @JsonProperty("campus")
  public CampusDto campus;
}
