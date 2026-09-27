package ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers;

import ca.ulaval.glo4002.application.interfaces.rest.dto.ErrorResponseBuilder;
import jakarta.annotation.Priority;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;

@Provider
@Priority(Priorities.USER)
public class CustomConstraintValidationExceptionMapper
    implements
      ExceptionMapper<ConstraintViolationException>{

  @Override
  public Response toResponse(ConstraintViolationException exception){
    String violations = exception.getConstraintViolations().stream()
        .map(ConstraintViolation::getMessage).reduce((a,b) -> a + ", " + b)
        .orElse("validation error");

    String message = "Invalid request: " + violations;

    return ErrorResponseBuilder.buildBadRequest(message);
  }
}
