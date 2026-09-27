package ca.ulaval.glo4002.application.domain.access.accessRules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ZoneAccessDuringFireRuleTest{

  private ZoneAccessDuringFireRule zoneAccessDuringFireRule;
  private AccessContext accessContext;

  private final boolean ZONE_HAS_EMERGENCY = true;
  private final boolean USER_IS_AGENT = true;
  private final boolean USER_IS_NOT_AGENT = false;
  private final boolean ROOM_IS_SPECIFIED = true;
  private final boolean ROOM_IS_NOT_SPECIFIED = false;
  private final boolean USER_IS_LAB_RESPONSIBLE_IN_ROOM = true;
  private final boolean USER_IS_NOT_LAB_RESPONSIBLE_IN_ROOM = false;
  private final boolean USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING = false;
  private final boolean USER_IS_LAB_RESPONSIBLE_IN_BUILDING = true;

  @BeforeEach
  void setUp(){
    zoneAccessDuringFireRule = new ZoneAccessDuringFireRule();
    accessContext = mock(AccessContext.class);
  }

  @Test
  void givenAccessRequestedForAgentDuringEmergency_whenEvaluate_thenShouldReturnAllowed() {
    when(accessContext.doesSpecifiedZoneHaveFireOrPrelarmState()).thenReturn(ZONE_HAS_EMERGENCY);
    when(accessContext.isAccessRequestedForRoom()).thenReturn(ROOM_IS_SPECIFIED);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_AGENT);
    when(accessContext.isLabResponsibleForRoom()).thenReturn(USER_IS_LAB_RESPONSIBLE_IN_ROOM);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = zoneAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED, status);
  }

  @Test
  void givenAccessRequestedWhenNoEmergency_whenEvaluate_thenShouldReturnAllowed(){
    boolean zoneIsNotInEmergencyState = false;
    when(accessContext.doesSpecifiedZoneHaveFireOrPrelarmState())
        .thenReturn(zoneIsNotInEmergencyState);
    when(accessContext.isAccessRequestedForRoom()).thenReturn(ROOM_IS_SPECIFIED);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_AGENT);
    when(accessContext.isLabResponsibleForRoom()).thenReturn(USER_IS_LAB_RESPONSIBLE_IN_ROOM);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = zoneAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED,status);
  }

  @Test
  void
      givenAccessRequestedForLabResponsibleInRoomDuringFireOrPrealarm_whenEvaluate_thenShouldReturnAllowed() {
    when(accessContext.doesSpecifiedZoneHaveFireOrPrelarmState()).thenReturn(ZONE_HAS_EMERGENCY);
    when(accessContext.isAccessRequestedForRoom()).thenReturn(ROOM_IS_SPECIFIED);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_AGENT);
    when(accessContext.isLabResponsibleForRoom()).thenReturn(USER_IS_LAB_RESPONSIBLE_IN_ROOM);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = zoneAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED, status);
  }

  @Test
  void
      givenAccessRequestedForRandomUserInRoomDuringEmergency_whenEvaluate_thenShouldReturnDenied() {
    when(accessContext.doesSpecifiedZoneHaveFireOrPrelarmState()).thenReturn(ZONE_HAS_EMERGENCY);
    when(accessContext.isAccessRequestedForRoom()).thenReturn(ROOM_IS_SPECIFIED);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_NOT_AGENT);
    when(accessContext.isLabResponsibleForRoom()).thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_ROOM);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = zoneAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.DENIED, status);
  }

  @Test
  void
      givenAccessRequestedForZoneByLabResponsibleInZoneDuringEmergency_whenEvaluate_thenShouldReturnAllowed() {
    when(accessContext.doesSpecifiedZoneHaveFireOrPrelarmState()).thenReturn(ZONE_HAS_EMERGENCY);
    when(accessContext.isAccessRequestedForRoom()).thenReturn(ROOM_IS_NOT_SPECIFIED);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_NOT_AGENT);
    when(accessContext.isLabResponsibleForRoom()).thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_ROOM);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = zoneAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED, status);
  }

  @Test
  void
      givenAccessRequestedForZoneByRandomUserDuringEmergency_whenEvaluate_thenShouldReturnDenied() {
    when(accessContext.doesSpecifiedZoneHaveFireOrPrelarmState()).thenReturn(ZONE_HAS_EMERGENCY);
    when(accessContext.isAccessRequestedForRoom()).thenReturn(ROOM_IS_NOT_SPECIFIED);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_NOT_AGENT);
    when(accessContext.isLabResponsibleForRoom()).thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_ROOM);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = zoneAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.DENIED, status);
  }
}
