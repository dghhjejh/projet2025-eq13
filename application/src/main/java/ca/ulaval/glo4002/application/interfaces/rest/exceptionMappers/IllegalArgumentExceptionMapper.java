package ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers;

import ca.ulaval.glo4002.application.interfaces.rest.dto.ErrorResponse;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class IllegalArgumentExceptionMapper implements ExceptionMapper<IllegalArgumentException>{

  public IllegalArgumentExceptionMapper() {
  }

  @Override
  public Response toResponse(IllegalArgumentException exception){
    return Response.status(Response.Status.BAD_REQUEST)
        .entity(new ErrorResponse(exception.getMessage())).build();
  }
}
