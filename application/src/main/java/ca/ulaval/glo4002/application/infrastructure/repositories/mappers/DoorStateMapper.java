package ca.ulaval.glo4002.application.infrastructure.repositories.mappers;

import ca.ulaval.glo4002.application.domain.campus.doors.Door;
import ca.ulaval.glo4002.application.infrastructure.factories.DoorFactory;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.DoorStateDTO;

public class DoorStateMapper{

  public DoorStateDTO toDoorStateDTO(Door door){
    return new DoorStateDTO(door.getId().toString(),door.getIsLocked(),door.getIsOpen(),
        door.getInitialLockedState(),door.getInitialOpenState());
  }

  public Door toDoor(Door structureDoor,DoorStateDTO stateDTO){
    if (stateDTO == null){
      return structureDoor;
    }

    return DoorFactory.createDoor(structureDoor.getId(),structureDoor.getType(),
        structureDoor.getOnEvacuationPath(),stateDTO.isLocked(),stateDTO.isOpen(),
        stateDTO.initialLocked(),stateDTO.initialOpen());
  }
}
