package ca.ulaval.glo4002.application.interfaces.rest.dto;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class ErrorResponseBuilder{

  public static Response buildBadRequest(String message){
    return Response.status(Response.Status.BAD_REQUEST).entity(new ErrorResponse(message))
        .type(MediaType.APPLICATION_JSON_TYPE).build();
  }
}
