package ca.ulaval.glo4002.application.interfaces.rest.dto;

import ca.ulaval.glo4002.application.interfaces.exception.UnknownEventNameException;
import com.fasterxml.jackson.databind.JsonMappingException;
import java.time.format.DateTimeParseException;

public class InvalidFormatResponseBuilder{

  public static String buildMessage(JsonMappingException exception){
    String message = "Invalid request";

    Throwable cause = exception.getCause();

    if (cause instanceof DateTimeParseException){
      message += " : Invalid time format";
    } else if (cause instanceof UnknownEventNameException){
      message += " : Unknown event name";
    }

    return message;
  }
}
