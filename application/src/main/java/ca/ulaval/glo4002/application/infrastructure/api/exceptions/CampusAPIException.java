package ca.ulaval.glo4002.application.infrastructure.api.exceptions;

public class CampusAPIException extends RuntimeException{
  public CampusAPIException(String message) {
    super(message);
  }
}
