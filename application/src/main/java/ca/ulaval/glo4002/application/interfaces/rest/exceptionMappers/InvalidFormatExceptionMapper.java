package ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers;

import ca.ulaval.glo4002.application.interfaces.rest.dto.ErrorResponseBuilder;
import ca.ulaval.glo4002.application.interfaces.rest.dto.InvalidFormatResponseBuilder;
import com.fasterxml.jackson.databind.JsonMappingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class InvalidFormatExceptionMapper implements ExceptionMapper<JsonMappingException>{

  @Override
  public Response toResponse(JsonMappingException e){
    String message = InvalidFormatResponseBuilder.buildMessage(e);

    return ErrorResponseBuilder.buildBadRequest(message);
  }
}
