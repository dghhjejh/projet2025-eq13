package ca.ulaval.glo4002.application.infrastructure.repositories.campus;

import static ca.ulaval.glo4002.application.infrastructure.repositories.campus.BuildingInMemoryFixture.*;
import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.infrastructure.exceptions.BuildingNotFoundException;
import ca.ulaval.glo4002.application.infrastructure.repositories.BuildingInMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class BuildingInMemoryTest{
  private BuildingInMemory repository;

  @BeforeEach
  void createTestFixture(){
    BuildingInMemoryFixture fixture = new BuildingInMemoryFixture();
    repository = fixture.createTestRepository();
  }

  @Nested
  class FindByIdTest{

    @Nested
    class FindBuildingByIdTest{
      @Test
      void whenFindBuildingById_thenReturnExistingBuilding(){
        Building result = repository.findBuildingById(BUILDING_ID);

        assertEquals(BUILDING_ID,result.getId());
      }

      @Test
      void givenNonExistentBuildingId_whenFindBuildingById_thenThrowException(){
        assertThrows(BuildingNotFoundException.class,
            () -> repository.findBuildingById(NON_EXISTING_BUILDING_ID));
      }
    }
  }

  @Nested
  class GetParent{
    @Test
    void whenGetParentBuilding_thenReturnParentBuildingForZone(){
      Building result = repository.getParentBuilding(ZONE_ID);

      assertEquals(BUILDING_ID,result.getId());
    }

    @Test
    void givenNonExistingZone_whenGetParentBuilding_thenThrowException(){
      assertThrows(BuildingNotFoundException.class,
          () -> repository.getParentBuilding(NON_EXISTING_ZONE_ID));
    }
  }

  @Nested
  class ClearTest{
    @Test
    void whenClear_thenRemoveAllBuildings(){
      repository.findBuildingById(BuildingInMemoryFixture.BUILDING_ID);

      assertFalse(repository.getBuildings().isEmpty());

      repository.clear();

      assertTrue(repository.getBuildings().isEmpty());
    }
  }
}
