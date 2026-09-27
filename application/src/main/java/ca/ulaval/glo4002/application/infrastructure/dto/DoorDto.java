package ca.ulaval.glo4002.application.infrastructure.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class DoorDto{
  @JsonProperty("id")
  public String id;

  @JsonProperty("type")
  public String type;

  @JsonProperty("name")
  public String name;

  @JsonProperty("isOpen")
  public Boolean isOpen;

  @JsonProperty("isLocked")
  public Boolean isLocked;

  @JsonProperty("onEvacuationPath")
  public Boolean onEvacuationPath;
}
