package ca.ulaval.glo4002.application.domain.event.parameters;

import ca.ulaval.glo4002.application.domain.agents.InterventionId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.gathering.GatheringId;
import ca.ulaval.glo4002.application.domain.gathering.GatheringType;
import java.util.List;
import java.util.Objects;

public class EventParameterReader{

  public int extractConcentration(List<EventParameter> parameters){
    return parameters.stream().filter(parameter -> Objects.equals(parameter.name(),"concentration"))
        .findAny().map(parameter -> Integer.parseInt(parameter.value()))
        .orElseThrow(() -> new IllegalArgumentException("No Concentration found"));
  }

  public RoomId extractRoomIdFromParameter(List<EventParameter> parameters){
    return parameters.stream().filter(parameter -> Objects.equals(parameter.name(),"roomId"))
        .findAny().map(parameter -> new RoomId(parameter.value()))
        .orElseThrow(() -> new IllegalArgumentException("No RoomId found"));
  }

  public InterventionId extractInterventionId(List<EventParameter> parameters){
    return parameters.stream()
        .filter(parameter -> Objects.equals(parameter.name(),"interventionId")).findAny()
        .map(parameter -> new InterventionId(parameter.value()))
        .orElseThrow(() -> new IllegalArgumentException("No InterventionId found"));
  }

  public GatheringId extractGatheringId(List<EventParameter> parameters){
    return parameters.stream().filter(parameter -> Objects.equals(parameter.name(),"gatheringId"))
        .findAny().map(parameter -> new GatheringId(parameter.value()))
        .orElseThrow(() -> new IllegalArgumentException("No GatheringId found"));
  }

  public int extractExpectedAttendees(List<EventParameter> parameters){
    return parameters.stream()
        .filter(parameter -> Objects.equals(parameter.name(),"expectedAttendees")).findAny()
        .map(parameter -> Integer.parseInt(parameter.value()))
        .orElseThrow(() -> new IllegalArgumentException("No ExpectedAttendees found"));
  }

  public GatheringType extractGatheringType(List<EventParameter> parameters){
    return parameters.stream().filter(parameter -> Objects.equals(parameter.name(),"gatheringType"))
        .findAny().map(parameter -> GatheringType.valueOf(parameter.value()))
        .orElseThrow(() -> new IllegalArgumentException("No GatheringType found"));
  }
}
