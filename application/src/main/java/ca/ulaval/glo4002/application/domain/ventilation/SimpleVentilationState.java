package ca.ulaval.glo4002.application.domain.ventilation;

public enum SimpleVentilationState{
  OPEN("ouverte"), CLOSED("fermée");

  private final String translatedName;

  SimpleVentilationState(String translatedName) {
    this.translatedName = translatedName;
  }

  public static SimpleVentilationState fromString(String text){
    for (SimpleVentilationState state : SimpleVentilationState.values()){
      if (state.translatedName.equalsIgnoreCase(text)){
        return state;
      }
    }
    throw new IllegalArgumentException("No enum constant with translated name: " + text);
  }

  public static SimpleVentilationState fromInt(int value){
    return value == 1 ? OPEN : CLOSED;
  }

  public int toInt(){
    return this == OPEN ? 1 : 0;
  }

  @Override
  public String toString(){
    return this.translatedName;
  }
}
