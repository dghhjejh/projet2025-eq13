package ca.ulaval.glo4002.application.app;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SystemReinitializationServiceTest{

  private SystemReinitializationService systemReinitializationService;
  private BuildingRepository buildingRepository;

  @BeforeEach
  public void createService(){
    this.buildingRepository = mock(BuildingRepository.class);
    this.systemReinitializationService = new SystemReinitializationService(buildingRepository);
  }

  @Test
  public void whenResetBuildingMap_thenBuildingRepositoryIsCleared(){
    systemReinitializationService.resetBuildingMap();

    verify(buildingRepository).clear();
  }
}
