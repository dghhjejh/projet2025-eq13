package ca.ulaval.glo4002.application.infrastructure.repositories.mappers;

import ca.ulaval.glo4002.application.domain.gathering.Gathering;
import ca.ulaval.glo4002.application.domain.gathering.GatheringFactory;
import ca.ulaval.glo4002.application.domain.gathering.GatheringId;
import ca.ulaval.glo4002.application.domain.gathering.GatheringType;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.GatheringDTO;

public class GatheringMapper{

  public GatheringDTO toGatheringDTO(Gathering gathering,String zoneId){
    return new GatheringDTO(gathering.getGatheringId().toString(),gathering.getExpectedAttendees(),
        gathering.getGatheringType().toString(),zoneId);
  }

  public Gathering toGathering(GatheringDTO dto){
    if (dto == null || dto.gatheringType() == null){
      return null;
    }

    return GatheringFactory.createGathering(new GatheringId(dto.gatheringId()),
        dto.expectedAttendees(),GatheringType.valueOf(dto.gatheringType()));
  }
}
