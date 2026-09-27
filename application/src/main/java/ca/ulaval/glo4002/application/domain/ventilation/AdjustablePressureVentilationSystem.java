package ca.ulaval.glo4002.application.domain.ventilation;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.AdjustVentilationSpeed;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Map;

public class AdjustablePressureVentilationSystem implements VentilationSystem{
  private static final double NEGATIVE_PRESSURE_THRESHOLD = -60;
  private int distributionSpeed = 0;
  private int returnSpeed = 0;

  public AdjustablePressureVentilationSystem(Map<String, String> initialState) {
    this.returnSpeed = Integer.parseInt(initialState.get("returnSpeed"));
    this.distributionSpeed = Integer.parseInt(initialState.get("distributionSpeed"));
  }

  private void setSpeedValues(int medianSpeed,int pressureVariation){
    this.distributionSpeed = medianSpeed + pressureVariation;
    this.returnSpeed = 2 * medianSpeed - distributionSpeed;
  }

  public AdjustablePressureVentilationSystem() {
  }

  @Override
  public Action adjustVentilation(BuildingId buildingId,ZoneId zoneId,int medianSpeed,
      int pressureVariation,SimpleVentilationState isOpen,ActionBuilder actionBuilder){
    setSpeedValues(medianSpeed,pressureVariation);
    return new AdjustVentilationSpeed(buildingId,zoneId,this.distributionSpeed,this.returnSpeed);
  }

  @Override
  public Map<String, String> getCurrentState(){
    return Map.of("distributionSpeed",Integer.toString(this.distributionSpeed),"returnSpeed",
        Integer.toString(this.returnSpeed));
  }

  @Override
  public Boolean hasExtremeNegativePressure(){
    return this.distributionSpeed - this.returnSpeed <= NEGATIVE_PRESSURE_THRESHOLD;
  }
}
