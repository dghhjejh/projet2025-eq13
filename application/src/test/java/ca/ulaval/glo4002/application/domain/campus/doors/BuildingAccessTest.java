package ca.ulaval.glo4002.application.domain.campus.doors;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.enums.ActionName;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class BuildingAccessTest{
  private static final DoorId THIS_DOOR_ID = new DoorId("PLT200-ACCESS-01");
  private static final ZoneId PARENT_ZONE_ID = new ZoneId("PLT200");
  private static final BuildingId PARENT_BUILDING_ID = new BuildingId("PLT");
  private static final boolean LOCKED = true;
  private static final boolean UNLOCKED = false;
  private static final boolean OPEN = true;
  private static final boolean CLOSED = false;
  private static final boolean IS_ON_EVACUATION_PATH = true;
  private static final int ONE_ACTION_TYPE_EXPECTED = 1;
  private static final int ZERO_ACTION_TYPE_EXPECTED = 0;
  private BuildingAccess buildingAccessDoor;
  private final ActionBuilder actionBuilder = new ActionBuilder();

  @BeforeEach
  void setUp(){
    boolean onEvacuationPath = true;
    boolean isInitiallyLocked = true;
    boolean isInitiallyOpen = false;
    buildingAccessDoor = new BuildingAccess(THIS_DOOR_ID,onEvacuationPath,isInitiallyLocked,
        isInitiallyOpen,isInitiallyLocked,isInitiallyOpen);
  }

  @Test
  void givenIsLocked_whenTriggerFireEmergency_thenShouldNotReturnLockAction(){
    buildingAccessDoor.setIsLocked(LOCKED);

    List<Action> actions = buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,
        PARENT_ZONE_ID);

    assertEquals(ZERO_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.LockUnlockDoor));
  }

  @Test
  void givenIsUnlocked_whenTriggerFireEmergency_thenShouldReturnLockAction(){
    buildingAccessDoor.setIsLocked(UNLOCKED);

    List<Action> actions = buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,
        PARENT_ZONE_ID);

    assertEquals(ONE_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.LockUnlockDoor));
  }

  @Test
  void givenIsClosed_whenTriggerFireEmergency_thenShouldNotReturnCloseAction(){
    buildingAccessDoor.setIsOpen(CLOSED);

    List<Action> actions = buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,
        PARENT_ZONE_ID);

    assertEquals(ZERO_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.OpenCloseDoor));
  }

  @Test
  void givenIsOpen_whenTriggerFireEmergency_thenShouldReturnCloseAction(){
    buildingAccessDoor.setIsOpen(OPEN);

    List<Action> actions = buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,
        PARENT_ZONE_ID);

    assertEquals(ONE_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.OpenCloseDoor));
  }

  @Test
  void givenIsInitiallyLocked_whenResolveFireExtinguished_thenShouldNotReturnUnlockAction(){
    buildingAccessDoor = new BuildingAccess(THIS_DOOR_ID,IS_ON_EVACUATION_PATH,LOCKED,CLOSED,LOCKED,
        CLOSED);
    buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,PARENT_ZONE_ID);

    List<Action> actions = buildingAccessDoor.resolveFireExtinguished(actionBuilder,
        PARENT_BUILDING_ID,PARENT_ZONE_ID);

    assertEquals(ZERO_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.LockUnlockDoor));
  }

  @Test
  void givenIsInitiallyUnlocked_whenResolveFireExtinguished_thenShouldReturnUnlockAction(){
    buildingAccessDoor = new BuildingAccess(THIS_DOOR_ID,IS_ON_EVACUATION_PATH,UNLOCKED,CLOSED,
        UNLOCKED,CLOSED);
    buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,PARENT_ZONE_ID);

    List<Action> actions = buildingAccessDoor.resolveFireExtinguished(actionBuilder,
        PARENT_BUILDING_ID,PARENT_ZONE_ID);

    assertEquals(ONE_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.LockUnlockDoor));
  }

  @Test
  void givenIsInitiallyClosed_whenResolveFireExtinguished_thenShouldNotReturnOpenAction(){
    buildingAccessDoor = new BuildingAccess(THIS_DOOR_ID,IS_ON_EVACUATION_PATH,LOCKED,CLOSED,LOCKED,
        CLOSED);
    buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,PARENT_ZONE_ID);

    List<Action> actions = buildingAccessDoor.resolveFireExtinguished(actionBuilder,
        PARENT_BUILDING_ID,PARENT_ZONE_ID);

    assertEquals(ZERO_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.OpenCloseDoor));
  }

  @Test
  void givenIsInitiallyOpen_whenResolveFireExtinguished_thenShouldReturnOpenAction(){
    buildingAccessDoor = new BuildingAccess(THIS_DOOR_ID,IS_ON_EVACUATION_PATH,LOCKED,OPEN,LOCKED,
        OPEN);
    buildingAccessDoor.triggerFireEmergency(actionBuilder,PARENT_BUILDING_ID,PARENT_ZONE_ID);

    List<Action> actions = buildingAccessDoor.resolveFireExtinguished(actionBuilder,
        PARENT_BUILDING_ID,PARENT_ZONE_ID);

    assertEquals(ONE_ACTION_TYPE_EXPECTED,
        countActionsOfTypeReturned(actions,ActionName.OpenCloseDoor));
  }

  private int countActionsOfTypeReturned(List<Action> actions,ActionName actionName){
    int nbOfActions = 0;
    for (Action action : actions){
      if (action.getName().equals(actionName)){
        nbOfActions++;
      }
    }
    return nbOfActions;
  }
}
