package ca.ulaval.glo4002.application.infrastructure.exceptions;

public class BuildingNotFoundException extends RuntimeException{
  public BuildingNotFoundException(String message) {
    super(message);
  }
}
