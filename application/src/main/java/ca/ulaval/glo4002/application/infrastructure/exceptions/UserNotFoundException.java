package ca.ulaval.glo4002.application.infrastructure.exceptions;

public class UserNotFoundException extends RuntimeException{
  public UserNotFoundException(String message) {
    super(message);
  }
}
