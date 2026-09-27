package ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers;

import ca.ulaval.glo4002.application.infrastructure.exceptions.BuildingNotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
public class BuildingNotFoundExceptionMapper implements ExceptionMapper<BuildingNotFoundException>{

  @Override
  public Response toResponse(BuildingNotFoundException exception){
    return Response.status(Response.Status.NOT_FOUND).entity(exception.getMessage()).build();
  }
}
