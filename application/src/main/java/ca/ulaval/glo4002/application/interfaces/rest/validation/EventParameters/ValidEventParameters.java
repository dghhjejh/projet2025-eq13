package ca.ulaval.glo4002.application.interfaces.rest.validation.EventParameters;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ValidEventParametersValidator.class)
@Documented
public @interface ValidEventParameters{
  String message() default "Invalid request";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
