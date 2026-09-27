package ca.ulaval.glo4002.application.interfaces.rest.mappers;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.domain.event.SecurityEvent;
import ca.ulaval.glo4002.application.domain.event.SecurityEventName;
import ca.ulaval.glo4002.application.domain.event.parameters.EventParameter;
import ca.ulaval.glo4002.application.interfaces.exception.UnknownEventNameException;
import ca.ulaval.glo4002.application.interfaces.exception.UnknownEventParameterNameException;
import ca.ulaval.glo4002.application.interfaces.rest.requests.SecurityEventRequest;
import java.time.LocalTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityEventMapperTest{

  private static final LocalTime TIME_OF_EVENT = LocalTime.parse("15:00:00");
  private static final String EVENT_ZONE = "PLT200";
  private static final String SMOKE_PRESENCE_EVENT_NAME = "Présence de fumée";
  private static final String PARAM_KEY = "parametre";
  private static final String VALUE_KEY = "valeur";

  private static final String PARAM_CONCENTRATION = "concentration";
  private static final String VALUE_CONCENTRATION = "50";

  private static final String EVENT_GATHERING_STARTED = "Rassemblement débuté";
  private static final String PARAM_GATHERING_ID = "identifiantRassemblement";
  private static final String PARAM_GATHERING_ID_TRANSLATED = "gatheringId";
  private static final String PARAM_EXPECTED_ATTENDEES = "nombrePersonnesPrevues";
  private static final String PARAM_EXPECTED_ATTENDEES_TRANSLATED = "expectedAttendees";
  private static final String PARAM_GATHERING_TYPE = "typeRassemblement";
  private static final String PARAM_GATHERING_TYPE_TRANSLATED = "gatheringType";

  private static final String VALUE_GATHERING_ID = "GATH123";
  private static final String VALUE_EXPECTED_ATTENDEES = "250";
  private static final String VALUE_GATHERING_TYPE = "ACADEMIQUE";

  private SecurityEventMapper securityEventMapper;
  private SecurityEventRequest securityEventRequest;

  @BeforeEach
  public void createMapper(){
    securityEventMapper = new SecurityEventMapper();
  }

  @BeforeEach
  public void buildRequest(){
    securityEventRequest = new SecurityEventRequest();

    securityEventRequest.nom = SMOKE_PRESENCE_EVENT_NAME;
    securityEventRequest.heure = TIME_OF_EVENT;
    securityEventRequest.zone = EVENT_ZONE;

    Map<String, String> defaultParameter = new HashMap<>();
    defaultParameter.put(PARAM_KEY,PARAM_CONCENTRATION);
    defaultParameter.put(VALUE_KEY,VALUE_CONCENTRATION);

    securityEventRequest.parametres = List.of(defaultParameter);
  }

  @Test
  public void whenToSecurityEvent_thenMapsCorrectlySecurityEvent(){
    SecurityEvent securityEvent = securityEventMapper.toSecurityEvent(securityEventRequest);

    assertSecurityEventHasCorrectAttributes(securityEventRequest,securityEvent);
  }

  private void assertSecurityEventHasCorrectAttributes(SecurityEventRequest request,
      SecurityEvent event){

    assertNameCorresponds(request.nom,event.name());
    assertEquals(request.heure,event.time());
    assertEquals(request.zone,event.zoneId().toString());

    for (EventParameter param : event.parameters()){
      boolean match = request.parametres.stream()
          .anyMatch(p -> Objects.equals(p.get(PARAM_KEY),param.name())
              && Objects.equals(p.get(VALUE_KEY),param.value()));

      assertTrue(match,"Expected matching parameter for: " + param.name());
    }
  }

  private void assertNameCorresponds(String requestName,SecurityEventName eventName){
    SecurityEventName expectedName = switch (requestName){
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
      default -> throw new AssertionError("Unknown event name: " + requestName);
    };
    assertEquals(expectedName,eventName,"Event names should correspond");
  }

  @Test
  public void givenNullRequest_whenToSecurityEvent_thenThrowsIllegalArgumentException(){
    assertThrows(IllegalArgumentException.class,() -> securityEventMapper.toSecurityEvent(null));
  }

  @Test
  public void givenUnknownEventName_whenToSecurityEvent_thenThrowsUnknownEventNameException(){
    securityEventRequest.nom = "Unknown Event";

    assertThrows(UnknownEventNameException.class,
        () -> securityEventMapper.toSecurityEvent(securityEventRequest));
  }

  @Test
  public void givenUnknownParameterName_whenToSecurityEvent_thenThrowsUnknownEventParameterNameException(){
    securityEventRequest.parametres = List
        .of(Map.of(PARAM_KEY,"invalidParam",VALUE_KEY,"someValue"));

    assertThrows(UnknownEventParameterNameException.class,
        () -> securityEventMapper.toSecurityEvent(securityEventRequest));
  }

  @Test
  public void givenEmptyParameters_whenToSecurityEvent_thenMapsWithEmptyParametersList(){
    securityEventRequest.parametres = new ArrayList<>();

    SecurityEvent securityEvent = securityEventMapper.toSecurityEvent(securityEventRequest);

    assertTrue(securityEvent.parameters().isEmpty());
  }

  @Test
  public void givenGatheringParameters_whenToSecurityEvent_thenMapsCorrectly(){
    securityEventRequest.nom = EVENT_GATHERING_STARTED;
    securityEventRequest.parametres = List.of(
        Map.of(PARAM_KEY,PARAM_GATHERING_ID,VALUE_KEY,VALUE_GATHERING_ID),
        Map.of(PARAM_KEY,PARAM_EXPECTED_ATTENDEES,VALUE_KEY,VALUE_EXPECTED_ATTENDEES),
        Map.of(PARAM_KEY,PARAM_GATHERING_TYPE,VALUE_KEY,VALUE_GATHERING_TYPE));

    SecurityEvent event = securityEventMapper.toSecurityEvent(securityEventRequest);

    assertEquals(SecurityEventName.GATHERING_STARTED,event.name());
    assertHasParameter(event,PARAM_GATHERING_ID_TRANSLATED,VALUE_GATHERING_ID);
    assertHasParameter(event,PARAM_EXPECTED_ATTENDEES_TRANSLATED,VALUE_EXPECTED_ATTENDEES);
    assertHasParameter(event,PARAM_GATHERING_TYPE_TRANSLATED,"ACADEMIC");
  }

  private void assertHasParameter(SecurityEvent event,String expectedName,String expectedValue){
    boolean found = event.parameters().stream()
        .anyMatch(p -> p.name().equals(expectedName) && p.value().equals(expectedValue));

    assertTrue(found,"Missing parameter: " + expectedName + "=" + expectedValue);
  }

  @Test
  public void givenGatheringIdParameter_whenToSecurityEvent_thenMapsCorrectly(){
    securityEventRequest.nom = EVENT_GATHERING_STARTED;
    securityEventRequest.parametres = List
        .of(Map.of(PARAM_KEY,PARAM_GATHERING_ID,VALUE_KEY,"XYZ789"));

    SecurityEvent event = securityEventMapper.toSecurityEvent(securityEventRequest);

    assertHasParameter(event,PARAM_GATHERING_ID_TRANSLATED,"XYZ789");
  }

  @Test
  public void givenExpectedAttendeesParameter_whenToSecurityEvent_thenMapsCorrectly(){
    securityEventRequest.nom = EVENT_GATHERING_STARTED;
    securityEventRequest.parametres = List
        .of(Map.of(PARAM_KEY,PARAM_EXPECTED_ATTENDEES,VALUE_KEY,"1000"));

    SecurityEvent event = securityEventMapper.toSecurityEvent(securityEventRequest);

    assertHasParameter(event,PARAM_EXPECTED_ATTENDEES_TRANSLATED,"1000");
  }

  @Test
  public void givenValidGatheringType_whenToSecurityEvent_thenMapsToEnumValue(){
    securityEventRequest.nom = EVENT_GATHERING_STARTED;
    securityEventRequest.parametres = List
        .of(Map.of(PARAM_KEY,PARAM_GATHERING_TYPE,VALUE_KEY,VALUE_GATHERING_TYPE));

    SecurityEvent event = securityEventMapper.toSecurityEvent(securityEventRequest);

    assertHasParameter(event,PARAM_GATHERING_TYPE_TRANSLATED,"ACADEMIC");
  }

  @Test
  public void givenUnknownGatheringType_whenToSecurityEvent_thenThrowsIllegalArgumentException(){
    securityEventRequest.nom = EVENT_GATHERING_STARTED;
    securityEventRequest.parametres = List
        .of(Map.of(PARAM_KEY,PARAM_GATHERING_TYPE,VALUE_KEY,"NOT_A_TYPE"));

    assertThrows(IllegalArgumentException.class,
        () -> securityEventMapper.toSecurityEvent(securityEventRequest));
  }
}
