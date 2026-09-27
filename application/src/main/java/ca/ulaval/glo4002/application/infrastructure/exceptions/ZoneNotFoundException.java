package ca.ulaval.glo4002.application.infrastructure.exceptions;

public class ZoneNotFoundException extends RuntimeException{
  public ZoneNotFoundException(String message) {
    super(message);
  }
}
