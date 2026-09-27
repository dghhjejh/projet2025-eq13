package ca.ulaval.glo4002.application.domain.ventilation;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import java.util.Map;

public interface VentilationSystem{
  Map<String, String> getCurrentState();

  Action adjustVentilation(BuildingId buildingId,ZoneId zoneId,int medianSpeed,
      int pressureVariation,SimpleVentilationState isOpen,ActionBuilder actionBuilder);

  Boolean hasExtremeNegativePressure();
}
