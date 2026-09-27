package ca.ulaval.glo4002.application.domain.gathering;

public enum GatheringType{
  ACADEMIC, PROTEST, SOCIAL_ACTIVITY, OTHER_RISKY, OTHER;

  public static String toString(GatheringType gatheringType){
    return switch (gatheringType){
      case ACADEMIC -> "ACADEMIC";
      case PROTEST -> "PROTEST";
      case SOCIAL_ACTIVITY -> "SOCIAL_ACTIVITY";
      case OTHER_RISKY -> "OTHER_RISKY";
      case OTHER -> "OTHER";
    };
  }
}
