package ca.ulaval.glo4002.application.domain.factories;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4002.application.domain.actions.*;
import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.actions.enums.FireFighterCallReason;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.buildings.PostalAddress;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ActionBuilderTest{

  private static final Priority A_PRIORITY = Priority.P1;
  private static final FireFighterCallReason A_REASON = FireFighterCallReason.FIRE;

  private ActionBuilder actionBuilder;
  private BuildingId buildingId;
  private ZoneId zoneId;
  private RoomId roomId;
  private DoorId doorId;
  private PostalAddress postalAddress;

  @BeforeEach
  void setUp(){
    actionBuilder = new ActionBuilder();
    initializeTestIds();
  }

  private void initializeTestIds(){
    buildingId = new BuildingId("A_BUILDING");
    zoneId = new ZoneId("A_ZONE");
    roomId = new RoomId("A_ROOM");
    doorId = new DoorId("A_DOOR");
    postalAddress = new PostalAddress("123 Test St Test City A1A 1A1");
  }

  @Test
  void whenCreatingRequestAgentAction_thenReturnsRequestAgentInstance(){
    Action action = actionBuilder.createRequestAgentAction(zoneId,A_PRIORITY);

    assertEquals(ActionName.RequestAgent,action.getName());
  }

  @Test
  void whenCreatingActivateFireAlarmAction_thenReturnsActivateFireAlarmInstance(){
    boolean shouldActivate = true;
    Action action = actionBuilder.createActivateFireAlarmAction(buildingId,zoneId,shouldActivate);

    assertEquals(ActionName.ActivateFireAlarm,action.getName());
  }

  @Test
  void whenCreatingDeactivateFireAlarmAction_thenReturnsActivateFireAlarmInstance(){
    boolean shouldNotActivate = false;
    Action action = actionBuilder.createActivateFireAlarmAction(buildingId,zoneId,
        shouldNotActivate);

    assertEquals(ActionName.ActivateFireAlarm,action.getName());
  }

  @Test
  void whenCreatingCloseElectricityAction_thenReturnsOpenCloseElectricityInstance(){
    boolean isClosed = true;
    Action action = actionBuilder.createOpenCloseElectricityAction(buildingId,zoneId,roomId,
        isClosed);

    assertEquals(ActionName.OpenCloseElectricity,action.getName());
  }

  @Test
  void whenCreatingOpenElectricityAction_thenReturnsOpenCloseElectricityInstance(){
    boolean isOpen = false;
    Action action = actionBuilder.createOpenCloseElectricityAction(buildingId,zoneId,roomId,isOpen);

    assertEquals(ActionName.OpenCloseElectricity,action.getName());
  }

  @Test
  void whenCreatingOpenDoorAction_thenReturnsOpenCloseDoorInstance(){
    boolean isOpen = true;
    Action action = actionBuilder.createOpenCloseDoorAction(buildingId,zoneId,doorId,isOpen);

    assertEquals(ActionName.OpenCloseDoor,action.getName());
  }

  @Test
  void whenCreatingCloseDoorAction_thenReturnsOpenCloseDoorInstance(){
    boolean isClosed = false;
    Action action = actionBuilder.createOpenCloseDoorAction(buildingId,zoneId,doorId,isClosed);

    assertEquals(ActionName.OpenCloseDoor,action.getName());
  }

  @Test
  void whenCreatingLockDoorAction_thenReturnsLockUnlockDoorInstance(){
    boolean isLocked = true;
    Action action = actionBuilder.createLockUnlockDoorAction(buildingId,zoneId,doorId,isLocked);

    assertEquals(ActionName.LockUnlockDoor,action.getName());
  }

  @Test
  void whenCreatingUnlockDoorAction_thenReturnsLockUnlockDoorInstance(){
    boolean isNotLocked = false;
    Action action = actionBuilder.createLockUnlockDoorAction(buildingId,zoneId,doorId,isNotLocked);

    assertEquals(ActionName.LockUnlockDoor,action.getName());
  }

  @Test
  void whenCreatingCallFireFighterAction_thenReturnsCallFireFighterInstance(){
    Action action = actionBuilder.createCallFireFighterAction(postalAddress,A_REASON);

    assertEquals(ActionName.CallFireFighter,action.getName());
  }

  @Test
  void whenCreatingCloseVentilationAction_thenReturnsOpenCloseVentilationInstance(){
    int isOpen = 1;
    Action action = actionBuilder.createOpenCloseVentilationAction(buildingId,zoneId,isOpen);

    assertEquals(ActionName.OpenCloseVentilation,action.getName());
  }

  @Test
  void whenCreatingOpenVentilationAction_thenReturnsOpenCloseVentilationInstance(){
    int isClosed = 0;
    Action action = actionBuilder.createOpenCloseVentilationAction(buildingId,zoneId,isClosed);

    assertEquals(ActionName.OpenCloseVentilation,action.getName());
  }

  @Test
  void whenCreatingAdjustVentilationAction_thenReturnsAdjustVentilationInstance(){
    int aDistributionSpeed = 0;
    int aReturnSpeed = 100;
    Action action = actionBuilder.createAdjustVentilationSpeedAction(buildingId,zoneId,
        aDistributionSpeed,aReturnSpeed);

    assertEquals(ActionName.AdjustVentilationSpeed,action.getName());
  }
}
