package ca.ulaval.glo4002.application.infrastructure.factories;

import ca.ulaval.glo4002.application.domain.campus.doors.*;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;

public class DoorFactory{

  public static Door createDoor(DoorId doorId,DoorType doorType,Boolean onEvacuationPath,
      Boolean isLocked,Boolean isOpen,Boolean isInitiallyLocked,Boolean isInitiallyOpen){
    return switch (doorType){
      case FIRE_DOOR ->
        new FireDoor(doorId,onEvacuationPath,isLocked,isOpen,isInitiallyLocked,isInitiallyOpen);
      case LOCKABLE ->
        new Lockable(doorId,onEvacuationPath,isLocked,isOpen,isInitiallyLocked,isInitiallyOpen);
      case NORMAL ->
        new Normal(doorId,onEvacuationPath,isLocked,isOpen,isInitiallyLocked,isInitiallyOpen);
      case BUILDING_ACCESS -> new BuildingAccess(doorId,onEvacuationPath,isLocked,isOpen,
          isInitiallyLocked,isInitiallyOpen);
    };
  }
}
