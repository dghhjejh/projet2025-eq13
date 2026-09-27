package ca.ulaval.glo4002.application.domain.campus.zones;

public enum UrgencyState{
  FIRE("CONFIRMÉ"), PREALARM("PROBABLE"), NORMAL("AUCUN");

  private final String translatedName;

  UrgencyState(String translatedName) {
    this.translatedName = translatedName;
  }

  public static UrgencyState fromString(String text){
    for (UrgencyState state : UrgencyState.values()){
      if (state.translatedName.equalsIgnoreCase(text)){
        return state;
      }
    }
    throw new IllegalArgumentException("No enum constant with translated name: " + text);
  }

  @Override
  public String toString(){
    return this.translatedName;
  }
}
