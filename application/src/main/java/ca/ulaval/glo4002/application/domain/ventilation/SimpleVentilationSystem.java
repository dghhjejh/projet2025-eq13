package ca.ulaval.glo4002.application.domain.ventilation;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Map;

public class SimpleVentilationSystem implements VentilationSystem{

  private SimpleVentilationState isOpen = SimpleVentilationState.OPEN;

  public SimpleVentilationSystem(Map<String, String> initialState) {
    this.isOpen = SimpleVentilationState.fromString(initialState.get("isOpen"));
  }

  public SimpleVentilationSystem() {
  }

  @Override
  public Action adjustVentilation(BuildingId buildingId,ZoneId zoneId,int medianSpeed,
      int pressureVariation,SimpleVentilationState isOpen,ActionBuilder actionBuilder){
    this.isOpen = isOpen;
    return actionBuilder.createOpenCloseVentilationAction(buildingId,zoneId,this.isOpen.toInt());
  }

  @Override
  public Map<String, String> getCurrentState(){
    return Map.of("isOpen",isOpen.toString());
  }

  @Override
  public Boolean hasExtremeNegativePressure(){
    return false;
  }
}
