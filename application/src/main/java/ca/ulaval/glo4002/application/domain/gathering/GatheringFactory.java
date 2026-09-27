package ca.ulaval.glo4002.application.domain.gathering;

public class GatheringFactory{
  public static Gathering createGathering(GatheringId gatheringId,int expectedAttendees,
      GatheringType gatheringType){
    return switch (gatheringType){
      case PROTEST -> new HighRiskGathering(gatheringId,expectedAttendees,gatheringType);
      case OTHER_RISKY -> new MediumRiskGathering(gatheringId,expectedAttendees,gatheringType);
      case ACADEMIC, SOCIAL_ACTIVITY, OTHER ->
        new LowRiskGathering(gatheringId,expectedAttendees,gatheringType);
    };
  }
}
