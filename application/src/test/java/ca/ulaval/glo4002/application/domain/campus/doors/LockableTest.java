package ca.ulaval.glo4002.application.domain.campus.doors;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class LockableTest{

  private static final BuildingId BUILDING_ID = new BuildingId("PLT");
  private static final ZoneId ZONE_ID = new ZoneId("PLT200");
  private static final DoorId DOOR_ID = new DoorId("PLT200-BARRABLE-01");

  private static final boolean IS_UNLOCKED = false;
  private static final boolean ON_EVACUATION_PATH = true;
  private static final boolean NOT_ON_EVACUATION_PATH = false;
  private static final boolean IS_LOCKED = true;
  private static final boolean IS_INITIALLY_LOCKED = true;
  private static final boolean IS_INITIALLY_UNLOCKED = false;
  private static final boolean IS_OPEN = true;
  private static final boolean IS_CLOSED = false;
  private static final boolean IS_INITIALLY_CLOSED = false;
  private static final boolean IS_INITIALLY_OPEN = true;

  private Lockable initiallyLockedDoor;
  private Lockable initiallyUnlockedDoor;
  private Lockable notOnEvacuationPathDoor;
  private ActionBuilder actionBuilder;

  @BeforeEach
  public void setup(){
    actionBuilder = new ActionBuilder();

    initiallyLockedDoor = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_LOCKED,IS_CLOSED,
        IS_INITIALLY_LOCKED,IS_CLOSED);
    initiallyUnlockedDoor = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_CLOSED,
        IS_INITIALLY_UNLOCKED,IS_CLOSED);
    notOnEvacuationPathDoor = new Lockable(DOOR_ID,NOT_ON_EVACUATION_PATH,IS_LOCKED,IS_CLOSED,
        IS_INITIALLY_LOCKED,IS_CLOSED);
  }

  @Test
  void givenDoorLockedAndOnEvacuationPath_whenTriggerFireEmergency_thenUnlocksDoor(){
    initiallyLockedDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);

    assertFalse(initiallyLockedDoor.getIsLocked());
  }

  @Test
  void givenDoorLockedAndOnEvacuationPath_whenTriggerFireEmergency_thenReturnsLockUnlockAction(){
    List<Action> action = initiallyLockedDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,
        ZONE_ID);

    Action expectedAction = actionBuilder.createLockUnlockDoorAction(BUILDING_ID,ZONE_ID,DOOR_ID,
        false);

    assertEquals(expectedAction,action.getFirst());
  }

  @Test
  void givenDoorOnEvacuationPathAlreadyUnlocked_whenTriggerFireEmergency_thenNoActionCreated(){
    List<Action> action = initiallyUnlockedDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,
        ZONE_ID);

    assertTrue(action.isEmpty());
  }

  @Test
  void givenDoorNotOnEvacuationPath_whenTriggerFireEmergency_thenNoActionCreated(){
    List<Action> action = notOnEvacuationPathDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,
        ZONE_ID);

    assertTrue(action.isEmpty());
  }

  @Test
  void givenDoorWasInitiallyLockedAndNowUnlocked_whenResolveFireExtinguished_thenRelocksDoor(){
    initiallyLockedDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);
    boolean initialLockedState = initiallyLockedDoor.getIsLocked();

    initiallyLockedDoor.resolveFireExtinguished(actionBuilder,BUILDING_ID,ZONE_ID);
    boolean finalLockedState = initiallyLockedDoor.getIsLocked();

    assertEquals(!initialLockedState,finalLockedState);
    assertTrue(finalLockedState);
  }

  @Test
  void givenDoorWasInitiallyLockedAndNowUnlocked_whenResolveFireExtinguished_thenReturnsLockUnlockAction(){
    initiallyLockedDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);

    List<Action> actions = initiallyLockedDoor.resolveFireExtinguished(actionBuilder,BUILDING_ID,
        ZONE_ID);

    Action expectedAction = actionBuilder.createLockUnlockDoorAction(BUILDING_ID,ZONE_ID,DOOR_ID,
        true);
    assertFalse(actions.isEmpty());
    assertEquals(expectedAction,actions.getFirst());
  }

  @Test
  void givenDoorStateMatchesInitial_whenResolveFireExtinguished_thenNoActionCreated(){
    initiallyUnlockedDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);

    List<Action> actions = initiallyUnlockedDoor.resolveFireExtinguished(actionBuilder,BUILDING_ID,
        ZONE_ID);

    assertTrue(actions.isEmpty());
  }

  @Test
  void givenDoorNotOnEvacuationPath_whenResolveFireExtinguished_thenNoActionCreated(){
    notOnEvacuationPathDoor.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);

    List<Action> actions = notOnEvacuationPathDoor.resolveFireExtinguished(actionBuilder,
        BUILDING_ID,ZONE_ID);

    assertTrue(actions.isEmpty());
  }

  @Test
  void givenUnlockedDoor_whenApplyGatheringStarted_thenLocksDoor(){
    initiallyUnlockedDoor.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    assertTrue(initiallyUnlockedDoor.getIsLocked());
  }

  @Test
  void givenUnlockedDoor_whenApplyGatheringStarted_thenReturnsLockAction(){
    List<Action> actions = initiallyUnlockedDoor.applyGatheringStarted(actionBuilder,BUILDING_ID,
        ZONE_ID);

    Action expectedAction = actionBuilder.createLockUnlockDoorAction(BUILDING_ID,ZONE_ID,DOOR_ID,
        true);
    assertEquals(List.of(expectedAction),actions);
  }

  @Test
  void givenOpenAndUnlockedDoor_whenApplyGatheringStarted_thenClosesAndLocksDoor(){
    Lockable openDoor = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_OPEN,
        IS_INITIALLY_UNLOCKED,IS_INITIALLY_OPEN);

    openDoor.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    assertFalse(openDoor.getIsOpen());
    assertTrue(openDoor.getIsLocked());
  }

  @Test
  void givenOpenAndUnlockedDoor_whenApplyGatheringStarted_thenReturnsLockDoorAction(){
    Lockable openDoor = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_OPEN,
        IS_INITIALLY_UNLOCKED,IS_OPEN);

    List<Action> action = openDoor.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    Action expectedLockAction = actionBuilder.createLockUnlockDoorAction(BUILDING_ID,ZONE_ID,
        DOOR_ID,true);
    assertEquals(expectedLockAction,action.getFirst());
  }

  @Test
  void givenAlreadyClosedAndLockedDoor_whenApplyGatheringStarted_thenNoActionCreated(){
    List<Action> actions = initiallyLockedDoor.applyGatheringStarted(actionBuilder,BUILDING_ID,
        ZONE_ID);

    assertEquals(new ArrayList<>(),actions);
  }

  @Test
  void givenDoorLockedButInitiallyUnlocked_whenApplyGatheringEnded_thenReturnsLockUnlockAction(){
    Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_LOCKED,IS_CLOSED,
        IS_INITIALLY_UNLOCKED,IS_CLOSED);
    door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    List<Action> actions = door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

    Action expectedAction = actionBuilder.createLockUnlockDoorAction(BUILDING_ID,ZONE_ID,DOOR_ID,
        false);
    assertEquals(List.of(expectedAction),actions);
  }

  @Test
  void givenDoorLockedButInitiallyUnlocked_whenApplyGatheringEnded_thenUnlocksDoor(){
    Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_LOCKED,IS_CLOSED,
        IS_INITIALLY_UNLOCKED,IS_CLOSED);
    door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

    assertFalse(door.getIsLocked());
  }

  @Test
  void givenNoGatheringWasActive_whenApplyGatheringEnded_thenNoActionCreated(){
    List<Action> actions = initiallyUnlockedDoor.applyGatheringEnded(actionBuilder,BUILDING_ID,
        ZONE_ID);

    assertTrue(actions.isEmpty());
  }

  @Test
  void givenDoorStateMatchesInitial_whenApplyGatheringEnded_thenNoActionCreated(){
    Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_LOCKED,IS_CLOSED,IS_INITIALLY_LOCKED,
        IS_INITIALLY_CLOSED);
    door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    List<Action> actions = door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

    assertTrue(actions.isEmpty());
  }

  @Test
  void givenGatheringWasActive_whenApplyGatheringEnded_thenRestoresInitialState(){
    Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_CLOSED,
        IS_INITIALLY_UNLOCKED,IS_CLOSED);
    door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    List<Action> actions = door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

    Action expectedAction = actionBuilder.createLockUnlockDoorAction(BUILDING_ID,ZONE_ID,DOOR_ID,
        false);
    assertEquals(List.of(expectedAction),actions);
    assertFalse(door.getIsLocked());
  }

  @Test
  void givenDoorInitiallyOpenThenClosedForGathering_whenApplyGatheringEnded_thenRestoresInitialOpenState(){
    Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_OPEN,
        IS_INITIALLY_UNLOCKED,IS_INITIALLY_OPEN);
    door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

    assertTrue(door.getIsOpen());
  }

  @Test
  void givenDoorOpenStateChangedButLockMatchesInitial_whenApplyGatheringEnded_thenNoActionCreated(){
    Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_LOCKED,IS_OPEN,IS_INITIALLY_LOCKED,
        IS_CLOSED);
    door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

    List<Action> actions = door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

    assertTrue(actions.isEmpty());
  }

  @Nested
  class GatheringAndFireScenarios{

    @Test
    void givenFireActive_whenApplyGatheringStarted_thenNoActionIsCreated(){
      Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_CLOSED,
          IS_INITIALLY_UNLOCKED,IS_CLOSED);
      door.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);

      List<Action> actions = door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

      assertEquals(new ArrayList<>(),actions);
    }

    @Test
    void givenFireActive_whenApplyGatheringEnded_thenNoActionCreated(){
      Lockable door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_CLOSED,
          IS_INITIALLY_UNLOCKED,IS_CLOSED);
      door.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);

      List<Action> actions = door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

      assertTrue(actions.isEmpty());
    }

    @Nested
    class GivenGatheringActive_WhenTriggerFire{

      private Lockable door;
      private List<Action> action;

      @BeforeEach
      void givenGatheringActive_whenTriggerFire(){
        door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_CLOSED,IS_INITIALLY_UNLOCKED,
            IS_CLOSED);
        door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);

        action = door.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);
      }

      @Test
      void thenFireTakesPriorityAndUnlocksDoor(){
        assertFalse(door.getIsLocked());
      }

      @Test
      void thenAnActionIsReturned(){
        assertFalse(action.isEmpty());
      }

      @Test
      void thenActionIsUnlockDoorAction(){
        Action expected = actionBuilder.createLockUnlockDoorAction(BUILDING_ID,ZONE_ID,DOOR_ID,
            false);
        assertEquals(expected,action.getFirst());
      }
    }

    @Nested
    class GivenGatheringThenFire_WhenFireEnds{

      private Lockable door;
      private List<Action> result;

      @BeforeEach
      void givenGatheringThenFire_whenFireEnds(){
        door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_CLOSED,IS_INITIALLY_UNLOCKED,
            IS_CLOSED);
        door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);
        door.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);

        result = door.resolveFireExtinguished(actionBuilder,BUILDING_ID,ZONE_ID);
      }

      @Test
      void thenNoActionsAreReturned(){
        assertEquals(new ArrayList<>(),result);
      }

      @Test
      void thenDoorRemainsUnlocked(){
        assertFalse(door.getIsLocked());
      }
    }

    @Nested
    class GivenGatheringThenFireThenGatheringEnd_WhenFireEnds{

      private Lockable door;
      private List<Action> actions;

      @BeforeEach
      void givenGatheringThenFireThenGatheringEnd_whenFireEnds(){
        door = new Lockable(DOOR_ID,ON_EVACUATION_PATH,IS_UNLOCKED,IS_CLOSED,IS_INITIALLY_UNLOCKED,
            IS_CLOSED);
        door.applyGatheringStarted(actionBuilder,BUILDING_ID,ZONE_ID);
        door.triggerFireEmergency(actionBuilder,BUILDING_ID,ZONE_ID);
        door.applyGatheringEnded(actionBuilder,BUILDING_ID,ZONE_ID);

        actions = door.resolveFireExtinguished(actionBuilder,BUILDING_ID,ZONE_ID);
      }

      @Test
      void thenNoActionIsRequired(){
        assertEquals(new ArrayList<>(),actions);
      }

      @Test
      void thenDoorRestoresToInitialUnlockedState(){
        assertFalse(door.getIsLocked());
      }
    }
  }
}
