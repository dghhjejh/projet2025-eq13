package ca.ulaval.glo4002.application.app;

import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;

public class SystemReinitializationService{
  private final BuildingRepository buildingRepository;

  public SystemReinitializationService(BuildingRepository buildingRepository) {
    this.buildingRepository = buildingRepository;
  }

  public void resetBuildingMap(){
    this.buildingRepository.clear();
  }
}
