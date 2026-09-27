package ca.ulaval.glo4002.application.domain.gathering;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.zones.Zone;
import java.util.List;
import java.util.Objects;

public abstract class Gathering{
  final GatheringId gatheringId;
  final int expectedAttendees;
  final GatheringType gatheringType;

  public Gathering(GatheringId gatheringId,int expectedAttendees,GatheringType gatheringType) {
    this.gatheringId = gatheringId;
    this.expectedAttendees = expectedAttendees;
    this.gatheringType = gatheringType;
  }

  public abstract List<Action> determineSecurityActionsForGatheringStarted(Zone zone,
      BuildingId buildingId,ActionBuilder actionBuilder);

  public abstract List<Action> determineActionsForGatheringEnded(Zone zone,BuildingId buildingId,
      ActionBuilder actionBuilder);

  public GatheringType getGatheringType(){
    return gatheringType;
  }

  public GatheringId getGatheringId(){
    return gatheringId;
  }

  public int getExpectedAttendees(){
    return expectedAttendees;
  }

  public boolean isSocialOrRisky(){
    return gatheringType == GatheringType.SOCIAL_ACTIVITY
        || gatheringType == GatheringType.OTHER_RISKY;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (!(o instanceof Gathering that))
      return false;
    return this.expectedAttendees == that.expectedAttendees
        && this.gatheringId.equals(that.gatheringId) && this.gatheringType == that.gatheringType;
  }

  @Override
  public int hashCode(){
    return Objects.hash(this.gatheringId,this.expectedAttendees,this.gatheringType);
  }
}
