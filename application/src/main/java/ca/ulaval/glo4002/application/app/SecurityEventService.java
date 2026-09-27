package ca.ulaval.glo4002.application.app;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.event.SecurityEvent;
import jakarta.inject.Inject;
import java.util.List;

public class SecurityEventService{
  private final BuildingRepository buildingRepository;
  private final UserRepository userRepository;
  private final ActionBuilder actionBuilder;

  @Inject
  public SecurityEventService(BuildingRepository buildingRepository,ActionBuilder actionBuilder,
      UserRepository userRepository) {
    this.buildingRepository = buildingRepository;
    this.actionBuilder = actionBuilder;
    this.userRepository = userRepository;
  }

  public List<Action> processEvent(SecurityEvent securityEvent){
    Building building = this.buildingRepository.getParentBuilding(securityEvent.zoneId());

    List<Action> actions = building.handleEvent(securityEvent.zoneId(),securityEvent.name(),
        securityEvent.parameters(),this.actionBuilder,this.userRepository);

    this.buildingRepository.saveBuildingState(building);

    return actions;
  }

  public Building getBuilding(BuildingId buildingId){
    return buildingRepository.findBuildingById(buildingId);
  }
}
