package ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers;

import ca.ulaval.glo4002.application.interfaces.rest.dto.ConstraintValidationResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ConstraintValidationExceptionMapper
    implements
      ExceptionMapper<ConstraintViolationException>{

  private final Logger logger = LoggerFactory.getLogger(ConstraintValidationExceptionMapper.class);

  @Override
  public Response toResponse(ConstraintViolationException exception){
    ConstraintValidationResponse response = createResponse(exception);

    this.logger.error("{} : {}",response.message(),String.join(", ",response.validations()));

    return Response.status(Status.BAD_REQUEST).entity(response)
        .type(MediaType.APPLICATION_JSON_TYPE).build();
  }

  private ConstraintValidationResponse createResponse(ConstraintViolationException exception){
    var validations = exception.getConstraintViolations().stream().map(this::createMessage)
        .toList();
    return new ConstraintValidationResponse("Invalid input parameters",validations);
  }

  private String createMessage(ConstraintViolation<?> x){
    return x.getPropertyPath() + " " + x.getMessage() + " (received: " + x.getInvalidValue() + ")";
  }
}
