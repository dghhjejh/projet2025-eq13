package ca.ulaval.glo4002.application.interfaces.exception;

public class UnknownEventParameterNameException extends IllegalArgumentException{
  public UnknownEventParameterNameException(String message) {
    super(message);
  }
}
