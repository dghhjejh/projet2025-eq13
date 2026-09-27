package ca.ulaval.glo4002.application.interfaces.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

public record ErrorResponse(String message) {

  @JsonCreator
  public ErrorResponse {
  }
}
