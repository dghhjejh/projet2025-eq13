package ca.ulaval.glo4002.application.infrastructure.repositories.mappers;

import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.BuildingZoneMappingDTO;
import java.util.ArrayList;
import java.util.List;

public class BuildingZoneMappingMapper{

  public List<BuildingZoneMappingDTO> toBuildingZoneMappingDTOs(BuildingId buildingId,
      List<Zone> zones){
    List<BuildingZoneMappingDTO> mappings = new ArrayList<>();

    for (Zone zone : zones){
      mappings.add(new BuildingZoneMappingDTO(buildingId.toString(),zone.getId().toString()));
    }

    return mappings;
  }
}
