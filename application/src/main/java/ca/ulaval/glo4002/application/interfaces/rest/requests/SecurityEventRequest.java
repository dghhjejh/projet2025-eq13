package ca.ulaval.glo4002.application.interfaces.rest.requests;

import ca.ulaval.glo4002.application.interfaces.rest.validation.EventParameters.ValidEventParameters;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@ValidEventParameters
public class SecurityEventRequest{
  @NotNull(message = "Event name cannot be null")
  @NotBlank(message = "Event name cannot be blank")
  public String nom;

  @NotNull(message = "Time cannot be null")
  public LocalTime heure;

  @NotNull(message = "Zone cannot be null")
  @NotBlank(message = "Zone cannot be blank")
  public String zone;

  public List<Map<String, String>> parametres;
}
