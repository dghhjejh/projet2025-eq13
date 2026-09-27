package ca.ulaval.glo4002.application.infrastructure.factories;

import ca.ulaval.glo4002.application.domain.campus.buildings.VentilationSystemType;
import ca.ulaval.glo4002.application.domain.ventilation.AdjustablePressureVentilationSystem;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationSystem;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import java.util.Map;

public class VentilationSystemFactory{
  public VentilationSystem create(VentilationSystemType canAdjustPressure){
    if (canAdjustPressure == VentilationSystemType.CONTROLLABLE_SPEED){
      return new AdjustablePressureVentilationSystem();
    } else{
      return new SimpleVentilationSystem();
    }
  }

  public VentilationSystem createFromInitialState(VentilationSystemType canAdjustPressure,
      Map<String, String> initialState){
    if (canAdjustPressure == VentilationSystemType.CONTROLLABLE_SPEED){
      return new AdjustablePressureVentilationSystem(initialState);
    } else{
      return new SimpleVentilationSystem(initialState);
    }
  }
}
