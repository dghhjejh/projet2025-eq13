package ca.ulaval.glo4002.application.interfaces.rest.requests;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;

public class AccessRequest{

  public String card_id;

  @NotNull(message = "Zone cannot be null")
  @NotBlank(message = "Zone cannot be blank")
  public String zone_id;

  public String room_id;

  public String door_id;

  @NotNull(message = "Time cannot be null")
  public LocalTime date_heure;
}
