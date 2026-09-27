package ca.ulaval.glo4002.application.interfaces.rest.validation.EventParameters;

import ca.ulaval.glo4002.application.interfaces.rest.requests.SecurityEventRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ValidEventParametersValidator
    implements
      ConstraintValidator<ValidEventParameters, SecurityEventRequest>{

  private static final String PARAMETER_CONCENTRATION = "concentration";
  private static final String ROOM_ID = "roomId";
  private static final int CONCENTRATION_MIN = 1;
  private static final int CONCENTRATION_MAX = 100;
  private static final String PARAMETER = "parametre";
  private static final String VALUE = "valeur";
  private static final String PARAMETER_NO_INTERVENTION = "noIntervention";
  private static final String PARAMETER_GATHERING_ID = "identifiantRassemblement";
  private static final String PARAMETER_EXPECTED_ATTENDEES = "nombrePersonnesPrevues";
  private static final String PARAMETER_GATHERING_TYPE = "typeRassemblement";

  @Override
  public boolean isValid(SecurityEventRequest request,ConstraintValidatorContext context){
    if (request == null || request.nom == null)
      return true;

    return switch (request.nom){
      case "Présence de fumée" -> validateSmokePresence(request,context);
      case "Agent dépêché sur intervention", "Agent arrivé sur intervention",
          "Agent quitte intervention" ->
        validateNoInterventionParameter(request,context);
      case "Entrée sans carte", "Sortie de local" -> validateRoomId(request,context);
      case "Rassemblement débuté" -> validateGatheringId(request,context)
          && validateExpectedAttendees(request,context) && validateGatheringType(request,context);
      default -> true;
    };
  }

  private boolean validateExpectedAttendees(SecurityEventRequest request,
      ConstraintValidatorContext context){
    Optional<String> gatheringType = findParameters(request.parametres,
        PARAMETER_EXPECTED_ATTENDEES);

    if (gatheringType.isEmpty()){
      return fail(context,"'nombrePersonnesPrevues' parameter is required for this event.");
    }
    return true;
  }

  private boolean validateGatheringId(SecurityEventRequest request,
      ConstraintValidatorContext context){
    Optional<String> gatheringType = findParameters(request.parametres,PARAMETER_GATHERING_ID);

    if (gatheringType.isEmpty()){
      return fail(context,"'identifiantRassemblement parameter is required for this event.");
    }
    return true;
  }

  private boolean validateGatheringType(SecurityEventRequest request,
      ConstraintValidatorContext context){
    Optional<String> gatheringType = findParameters(request.parametres,PARAMETER_GATHERING_TYPE);

    if (gatheringType.isEmpty()){
      return fail(context,"'typeRassemblement' parameter is required for this event.");
    }
    return true;
  }

  private boolean validateRoomId(SecurityEventRequest request,ConstraintValidatorContext context){
    Optional<String> roomId = findParameters(request.parametres,ROOM_ID);

    if (roomId.isEmpty()){
      return fail(context,"'RoomId parameter is required for event 'Room exit'");
    }
    return true;
  }

  private boolean validateSmokePresence(SecurityEventRequest request,
      ConstraintValidatorContext context){
    Optional<String> concentrationOptional = findParameters(request.parametres,
        PARAMETER_CONCENTRATION);

    if (concentrationOptional.isEmpty()){
      return fail(context,"'Concentration' parameter is required for event 'Smoke presence'.");
    }
    try{
      int value = Integer.parseInt(concentrationOptional.get());

      if (value < CONCENTRATION_MIN || value > CONCENTRATION_MAX){
        return fail(context,"'Concentration' parameter must be an integer between 1 and 100.");
      }
    } catch (NumberFormatException e){
      return fail(context,"'Concentration' parameter must be an integer.");
    }
    return true;
  }

  private boolean validateNoInterventionParameter(SecurityEventRequest request,
      ConstraintValidatorContext context){
    Optional<String> noInterventionOptional = findParameters(request.parametres,
        PARAMETER_NO_INTERVENTION);

    if (noInterventionOptional.isEmpty()){
      return fail(context,"'noIntervention' parameter is required for this event.");
    }
    return true;
  }

  private Optional<String> findParameters(List<Map<String, String>> parameters,String name){
    if (parameters == null)
      return Optional.empty();

    return parameters.stream().filter(m -> name.equalsIgnoreCase(m.get(PARAMETER)))
        .map(m -> m.get(VALUE)).filter(v -> v != null && !v.isBlank()).findFirst();
  }

  private boolean fail(ConstraintValidatorContext context,String message){
    context.disableDefaultConstraintViolation();
    context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    return false;
  }
}
