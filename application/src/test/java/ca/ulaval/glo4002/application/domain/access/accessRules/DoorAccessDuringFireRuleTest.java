package ca.ulaval.glo4002.application.domain.access.accessRules;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.access.AccessContext;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DoorAccessDuringFireRuleTest{
  private DoorAccessDuringFireRule doorAccessDuringFireRule;
  private AccessContext accessContext;
  private final boolean BUILDING_IS_ON_FIRE = true;
  private final boolean USER_IS_NOT_AGENT = false;
  private final boolean USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING = false;
  private final boolean ACCESS_REQUESTED_FOR_BUILDING_ACCESS = true;
  private static final boolean USER_IS_AGENT = true;
  private static final boolean USER_IS_LAB_RESPONSIBLE_IN_BUILDING = true;
  private static final boolean ACCESS_REQUEST_IS_FOR_DOOR_IN_BUILDING = false;

  @BeforeEach
  void setUp(){
    doorAccessDuringFireRule = new DoorAccessDuringFireRule();
    accessContext = mock(AccessContext.class);
  }

  @Test
  void givenBuildingAccessDoorAndRandomUserDuringFire_whenEvaluate_thenShouldReturnDenied() {
    when(accessContext.buildingHasAnyZoneOnFire()).thenReturn(BUILDING_IS_ON_FIRE);
    when(accessContext.isAccessRequestedForBuildingAccess())
        .thenReturn(ACCESS_REQUESTED_FOR_BUILDING_ACCESS);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_NOT_AGENT);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = doorAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.DENIED, status);
  }

  @Test
  void givenBuildingAccessDoorRequestForAgentDuringFire_whenEvaluate_thenShouldReturnAllowed() {
    when(accessContext.buildingHasAnyZoneOnFire()).thenReturn(BUILDING_IS_ON_FIRE);
    when(accessContext.isAccessRequestedForBuildingAccess())
        .thenReturn(ACCESS_REQUESTED_FOR_BUILDING_ACCESS);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_AGENT);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_NOT_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = doorAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED, status);
  }

  @Test
  void
      givenBuildingAccessDoorRequestForLabResponsibleInBuildingDuringFire_whenEvaluate_thenShouldReturnAllowed() {
    when(accessContext.buildingHasAnyZoneOnFire()).thenReturn(BUILDING_IS_ON_FIRE);
    when(accessContext.isAccessRequestedForBuildingAccess())
        .thenReturn(ACCESS_REQUESTED_FOR_BUILDING_ACCESS);
    when(accessContext.isSecurityAgent()).thenReturn(USER_IS_NOT_AGENT);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(USER_IS_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = doorAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED, status);
  }

  @Test
  void givenAccessRequestForDoorInBuildingDuringFire_whenEvaluate_thenShouldReturnAllowed() {
    when(accessContext.buildingHasAnyZoneOnFire()).thenReturn(BUILDING_IS_ON_FIRE);
    when(accessContext.isAccessRequestedForBuildingAccess())
        .thenReturn(ACCESS_REQUEST_IS_FOR_DOOR_IN_BUILDING);
    when(accessContext.isSecurityAgent()).thenReturn(!USER_IS_AGENT);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(!USER_IS_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = doorAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED, status);
  }

  @Test
  void
      givenAccessRequestForBuildingAccessInBuildingWithNoEmergency_whenEvaluate_thenShouldReturnAllowed() {
    when(accessContext.buildingHasAnyZoneOnFire()).thenReturn(!BUILDING_IS_ON_FIRE);
    when(accessContext.isAccessRequestedForBuildingAccess())
        .thenReturn(ACCESS_REQUESTED_FOR_BUILDING_ACCESS);
    when(accessContext.isSecurityAgent()).thenReturn(!USER_IS_AGENT);
    when(accessContext.isUserIsALabResponsibleInThisBuilding())
        .thenReturn(!USER_IS_LAB_RESPONSIBLE_IN_BUILDING);

    AccessStatus status = doorAccessDuringFireRule.evaluate(accessContext);

    assertEquals(AccessStatus.ALLOWED, status);
  }
}
