package ca.ulaval.glo4002.application.domain.campus.rooms;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.RequestAgent;
import ca.ulaval.glo4002.application.domain.agents.Agent;
import ca.ulaval.glo4002.application.domain.bottin.*;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class LaboratoryTest{

  private static final BuildingId BUILDING_ID = new BuildingId("1");
  private static final ZoneId ZONE_ID = new ZoneId("A");
  private static final RoomId ROOM_ID = new RoomId("1");
  private static final Idul LAB_RESPONSIBLE_IDUL = new Idul("1");
  private static final PhoneNumber RESPONSIBLE_PHONE = new PhoneNumber("123");
  private static final String RESPONSIBLE_NAME = "John Doe";
  private static final boolean SUPPORTS_ELECTRICITY_CLOSURE = true;
  private static final PredefinedMessages FIRE_EVENT = PredefinedMessages.FIRE_ALERT_IN_ROOM_YRE_RESP;
  private static final PredefinedMessages FIRE_EXTINGUISHED = PredefinedMessages.END_OF_ALERT;

  private ActionBuilder actionBuilder;
  private UserRepository userRepository;
  private Laboratory laboratoryRoom;
  private User labResponsible;
  private final HashSet<Idul> notifiedResponsibles = new HashSet<>();

  @BeforeEach
  void setup(){
    actionBuilder = new ActionBuilder();
    userRepository = mock(UserRepository.class);
    labResponsible = mock(User.class);
    List<Agent> agentsDeployed = new ArrayList<>();
    List<Agent> agentsArrived = new ArrayList<>();
    List<RequestAgent> requestAgents = new ArrayList<>();

    when(labResponsible.contact_no()).thenReturn(RESPONSIBLE_PHONE);
    when(labResponsible.name()).thenReturn(RESPONSIBLE_NAME);
    when(userRepository.findUserByIDUL(LAB_RESPONSIBLE_IDUL)).thenReturn(labResponsible);
    laboratoryRoom = new Laboratory(ROOM_ID,SUPPORTS_ELECTRICITY_CLOSURE,LAB_RESPONSIBLE_IDUL,
        requestAgents,agentsDeployed,agentsArrived);
  }

  @Test
  void givenFireEvent_whenTriggerFire_Emergency_thenSendSMS(){
    List<Action> actions = laboratoryRoom.triggerFireEmergency(BUILDING_ID,ZONE_ID,actionBuilder,
        userRepository,notifiedResponsibles);

    Action expectedAction = actionBuilder.createSendPredefinedSMSAction(RESPONSIBLE_PHONE,
        RESPONSIBLE_NAME,FIRE_EVENT);
    assertTrue(actions.contains(expectedAction));
  }

  @Test
  void givenFireEventAndLabResponsibleAvailable_whenTriggerFire_Emergency_thenSendsTeams() {
    when(labResponsible.status()).thenReturn(Status.AVAILABLE);

    List<Action> actions =
        laboratoryRoom.triggerFireEmergency(
            BUILDING_ID, ZONE_ID, actionBuilder, userRepository, notifiedResponsibles);

    Action expectedAction =
        actionBuilder.createSendPredefinedTeamsAction(LAB_RESPONSIBLE_IDUL, FIRE_EVENT);
    assertTrue(actions.contains(expectedAction));
  }

  @Test
  void givenFireEventAndLabResponsibleUnavailable_whenTriggerFire_Emergency_thenSendsNoTeams() {
    when(labResponsible.status()).thenReturn(Status.IN_A_MEETING);

    List<Action> actions =
        laboratoryRoom.triggerFireEmergency(
            BUILDING_ID, ZONE_ID, actionBuilder, userRepository, notifiedResponsibles);

    Action expectedAction =
        actionBuilder.createSendPredefinedTeamsAction(LAB_RESPONSIBLE_IDUL, FIRE_EVENT);
    assertFalse(actions.contains(expectedAction));
  }

  @Test
  void givenFireExtinguished_whenTriggerFireEmergencyExtinguished_thenSendSMS(){
    List<Action> actions = laboratoryRoom.resolveFireExtinguished(BUILDING_ID,ZONE_ID,actionBuilder,
        userRepository,notifiedResponsibles);

    Action expectedAction = actionBuilder.createSendPredefinedSMSAction(RESPONSIBLE_PHONE,
        RESPONSIBLE_NAME,FIRE_EXTINGUISHED);
    assertTrue(actions.contains(expectedAction));
  }

  @Test
  void
      givenFireExtinguishedAndLabResponsibleAvailable_whenTriggerFireEmergencyExtinguished_thenSendsTeams() {
    when(labResponsible.status()).thenReturn(Status.AVAILABLE);

    List<Action> actions =
        laboratoryRoom.resolveFireExtinguished(
            BUILDING_ID, ZONE_ID, actionBuilder, userRepository, notifiedResponsibles);

    Action expectedAction =
        actionBuilder.createSendPredefinedTeamsAction(LAB_RESPONSIBLE_IDUL, FIRE_EXTINGUISHED);
    assertTrue(actions.contains(expectedAction));
  }

  @Test
  void
      givenFireExtinguishedAndLabResponsibleUnavailable_whenTriggerFireEmergencyExtinguished_thenSendsTeams() {
    when(labResponsible.status()).thenReturn(Status.OUT_OF_OFFICE);

    List<Action> actions =
        laboratoryRoom.resolveFireExtinguished(
            BUILDING_ID, ZONE_ID, actionBuilder, userRepository, notifiedResponsibles);

    Action expectedAction =
        actionBuilder.createSendPredefinedTeamsAction(LAB_RESPONSIBLE_IDUL, FIRE_EXTINGUISHED);
    assertTrue(actions.contains(expectedAction));
  }

  @Test
  void givenResponsibleAlreadyNotified_whenTriggerFire_Emergency_thenNoActions(){
    laboratoryRoom.triggerFireEmergency(BUILDING_ID,ZONE_ID,actionBuilder,userRepository,
        notifiedResponsibles);

    List<Action> actions = laboratoryRoom.triggerFireEmergency(BUILDING_ID,ZONE_ID,actionBuilder,
        userRepository,notifiedResponsibles);

    assertTrue(actions.isEmpty());
  }

  @Test
  void givenResponsibleAlreadyNotified_whenTriggerFireEmergencyExtinguished_thenNoActions(){
    laboratoryRoom.resolveFireExtinguished(BUILDING_ID,ZONE_ID,actionBuilder,userRepository,
        notifiedResponsibles);

    List<Action> actions = laboratoryRoom.resolveFireExtinguished(BUILDING_ID,ZONE_ID,actionBuilder,
        userRepository,notifiedResponsibles);

    assertTrue(actions.isEmpty());
  }
}
