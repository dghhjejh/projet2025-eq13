package ca.ulaval.glo4002.application.interfaces.exception;

public class UnknownEventNameException extends IllegalArgumentException{
  public UnknownEventNameException(String message) {
    super(message);
  }
}
