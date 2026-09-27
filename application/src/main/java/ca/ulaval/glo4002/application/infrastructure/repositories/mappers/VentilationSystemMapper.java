package ca.ulaval.glo4002.application.infrastructure.repositories.mappers;

import ca.ulaval.glo4002.application.domain.campus.buildings.VentilationSystemType;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationState;
import ca.ulaval.glo4002.application.domain.ventilation.VentilationSystem;
import ca.ulaval.glo4002.application.infrastructure.factories.VentilationSystemFactory;
import ca.ulaval.glo4002.application.infrastructure.repositories.dto.VentilationSystemDTO;
import java.util.HashMap;
import java.util.Map;

public class VentilationSystemMapper{
  private final VentilationSystemFactory ventilationSystemFactory;

  public VentilationSystemMapper(VentilationSystemFactory ventilationSystemFactory) {
    this.ventilationSystemFactory = ventilationSystemFactory;
  }

  public VentilationSystemDTO toVentilationSystemDTO(VentilationSystem ventilationSystem,
      String zoneId){
    if (ventilationSystem == null){
      return null;
    }

    Map<String, String> currentState = ventilationSystem.getCurrentState();

    Integer returnSpeed = null;
    Integer distributionSpeed = null;
    Integer isOpen = null;

    if (currentState.containsKey("returnSpeed") && currentState.containsKey("distributionSpeed")){
      returnSpeed = Integer.parseInt(currentState.get("returnSpeed"));
      distributionSpeed = Integer.parseInt(currentState.get("distributionSpeed"));
    } else if (currentState.containsKey("isOpen")){
      SimpleVentilationState state = SimpleVentilationState.fromString(currentState.get("isOpen"));
      isOpen = state.toInt();
    }

    return new VentilationSystemDTO(zoneId,returnSpeed,distributionSpeed,isOpen);
  }

  public VentilationSystem toVentilationSystem(VentilationSystemDTO dto,
      VentilationSystemType buildingVentilationType){

    if (dto == null){
      return ventilationSystemFactory.create(buildingVentilationType);
    }

    Map<String, String> state = new HashMap<>();

    if (dto.returnSpeed() != null && dto.distributionSpeed() != null){
      state.put("returnSpeed",String.valueOf(dto.returnSpeed()));
      state.put("distributionSpeed",String.valueOf(dto.distributionSpeed()));
    } else if (dto.isOpen() != null){
      state.put("isOpen",SimpleVentilationState.fromInt(dto.isOpen()).toString());
    }

    return ventilationSystemFactory.createFromInitialState(buildingVentilationType,state);
  }
}
