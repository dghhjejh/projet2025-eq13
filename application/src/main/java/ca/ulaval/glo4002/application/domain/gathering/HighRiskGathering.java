package ca.ulaval.glo4002.application.domain.gathering;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.enums.Priority;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import java.util.ArrayList;
import java.util.List;

public class HighRiskGathering extends Gathering{

  public static final int NUMBER_OF_AGENTS_TO_SEND = 2;

  public HighRiskGathering(GatheringId gatheringId,int expectedAttendees,
      GatheringType gatheringType) {
    super(gatheringId, expectedAttendees, gatheringType);
  }

  public List<Action> determineSecurityActionsForGatheringStarted(Zone zone,BuildingId buildingId,
      ActionBuilder actionBuilder){
    List<Action> actionsToSend = new ArrayList<>();

    actionsToSend
        .addAll(zone.sendAgentsForGathering(NUMBER_OF_AGENTS_TO_SEND,Priority.P2,actionBuilder));
    actionsToSend.addAll(zone.applyRulesForDoorsDuringGathering(buildingId,actionBuilder));

    return actionsToSend;
  }

  public List<Action> determineActionsForGatheringEnded(Zone zone,BuildingId buildingId,
      ActionBuilder actionBuilder){
    return zone.applyRulesForDoorsWhenGatheringEnds(buildingId,actionBuilder);
  }
}
