package ca.ulaval.glo4002.application.infrastructure.repositories.mappers;

import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.campus.rooms.OccupationCounter;
import ca.ulaval.glo4002.application.domain.campus.rooms.Room;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.RoomOccupantDTO;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.RoomOccupationDTO;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoomOccupationMapper{

  public RoomOccupationDTO toOccupationDTO(Room room){
    return new RoomOccupationDTO(room.getId().toString(),room.getCurrentOccupation(),true);
  }

  public List<RoomOccupantDTO> toOccupantDTOs(Room room){
    List<RoomOccupantDTO> occupants = new ArrayList<>();
    Map<Idul, Integer> consecutiveAccesses = room.getConsecutiveAccessesByPerson();

    for (Map.Entry<Idul, Integer> entry : consecutiveAccesses.entrySet()){
      occupants.add(
          new RoomOccupantDTO(room.getId().toString(),entry.getKey().toString(),entry.getValue()));
    }

    return occupants;
  }

  public OccupationCounter toOccupationCounter(RoomOccupationDTO occupationDTO,
      List<RoomOccupantDTO> occupantDTOs){
    if (occupationDTO == null || !occupationDTO.supportsCounting()){
      return null;
    }

    Map<Idul, Integer> occupants = new HashMap<>();
    for (RoomOccupantDTO occupantDTO : occupantDTOs){
      occupants.put(new Idul(occupantDTO.idul()),occupantDTO.consecutiveAccesses());
    }

    return new OccupationCounter(occupationDTO.currentOccupation(),occupants);
  }
}
