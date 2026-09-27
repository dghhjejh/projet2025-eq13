package ca.ulaval.glo4002.application.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.List;

public record ConstraintValidationResponse(String message, List<String> validations) {

  @JsonCreator
  public ConstraintValidationResponse {
  }
}
