package ca.ulaval.glo4002.application.interfaces.rest.mappers;

import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.event.SecurityEvent;
import ca.ulaval.glo4002.application.domain.event.SecurityEventName;
import ca.ulaval.glo4002.application.domain.event.parameters.EventParameter;
import ca.ulaval.glo4002.application.domain.gathering.GatheringType;
import ca.ulaval.glo4002.application.interfaces.exception.UnknownEventNameException;
import ca.ulaval.glo4002.application.interfaces.exception.UnknownEventParameterNameException;
import ca.ulaval.glo4002.application.interfaces.rest.requests.SecurityEventRequest;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SecurityEventMapper{

  private static final Map<String, String> PARAMETER_NAME_TRANSLATIONS = Map.of("concentration",
      "concentration","roomId","roomId","noIntervention","interventionId",
      "identifiantRassemblement","gatheringId","nombrePersonnesPrevues","expectedAttendees",
      "typeRassemblement","gatheringType");

  public SecurityEvent toSecurityEvent(SecurityEventRequest request){
    if (request == null){
      throw new IllegalArgumentException("Invalid request : SecurityEventRequest cannot be null");
    }

    List<EventParameter> domainEventParameters = mapEventParameters(request.parametres);
    SecurityEventName name = convertEventName(request.nom);
    ZoneId zoneId = new ZoneId(request.zone);
    return createSecurityEvent(name,zoneId,request.heure,domainEventParameters);
  }

  private List<EventParameter> mapEventParameters(List<Map<String, String>> requestEventParameters){
    if (requestEventParameters == null || requestEventParameters.isEmpty()){
      return new ArrayList<>();
    }

    return requestEventParameters.stream().map(this::mapToEventParameter)
        .collect(Collectors.toList());
  }

  private SecurityEventName convertEventName(String eventNameString){
    return switch (eventNameString){
      case "Préalarme d'incendie" -> SecurityEventName.FIRE_PREALARM;
      case "Préalarme expirée" -> SecurityEventName.EXPIRED_PREALARM;
      case "Préalarme annulée" -> SecurityEventName.CANCELLED_PREALARM;
      case "Préalarme confirmée" -> SecurityEventName.CONFIRMED_PREALARM;
      case "Incendie" -> SecurityEventName.FIRE;
      case "Incendie terminé" -> SecurityEventName.FIRE_ENDED;
      case "Agent dépêché sur intervention" -> SecurityEventName.AGENT_DEPLOYED_TO_INTERVENTION;
      case "Agent arrivé sur intervention" -> SecurityEventName.AGENT_ARRIVED_AT_INTERVENTION;
      case "Agent quitte intervention" -> SecurityEventName.AGENT_LEFT_INTERVENTION;
      case "Présence de fumée" -> SecurityEventName.SMOKE_PRESENCE;
      case "Entrée sans carte" -> SecurityEventName.NO_CARD_ENTRY;
      case "Sortie de local" -> SecurityEventName.ROOM_EXIT;
      case "Rassemblement débuté" -> SecurityEventName.GATHERING_STARTED;
      case "Rassemblement terminé" -> SecurityEventName.GATHERING_ENDED;
      default -> throw new UnknownEventNameException(
          "Invalid request : Event name '" + eventNameString + "' is unknown");
    };
  }

  public SecurityEvent createSecurityEvent(SecurityEventName name,ZoneId zoneId,LocalTime time,
      List<EventParameter> parameters){
    return new SecurityEvent(name,zoneId,time,parameters);
  }

  private EventParameter mapToEventParameter(Map<String, String> EventParameterMap){
    if (EventParameterMap == null || EventParameterMap.isEmpty()){
      throw new IllegalArgumentException(
          "Invalid request : EventParameter map cannot be null or empty");
    }

    String parameterName = EventParameterMap.get("parametre");
    String parameterValue = EventParameterMap.get("valeur");

    if (!PARAMETER_NAME_TRANSLATIONS.containsKey(parameterName)){
      throw new UnknownEventParameterNameException(
          "Invalid request : EventParameter " + parameterName + " does not exist");
    }

    String translatedParameterName = PARAMETER_NAME_TRANSLATIONS.get(parameterName);

    if ("typeRassemblement".equals(parameterName)){
      parameterValue = convertGatheringTypeValue(parameterValue);
    }

    return new EventParameter(translatedParameterName,parameterValue);
  }

  private String convertGatheringTypeValue(String parameterValue){
    return switch (parameterValue){
      case "ACADEMIQUE" -> GatheringType.ACADEMIC.toString();
      case "MANIFESTATION" -> GatheringType.PROTEST.toString();
      case "ACTIVITE_SOCIALE" -> GatheringType.SOCIAL_ACTIVITY.toString();
      case "AUTRE-RISQUEE" -> GatheringType.OTHER_RISKY.toString();
      case "AUTRE" -> GatheringType.OTHER.toString();
      default -> throw new IllegalArgumentException("Unknown gathering type: " + parameterValue);
    };
  }
}
