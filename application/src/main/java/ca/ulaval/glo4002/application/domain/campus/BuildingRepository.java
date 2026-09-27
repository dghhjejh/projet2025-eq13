package ca.ulaval.glo4002.application.domain.campus;

import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;

public interface BuildingRepository{

  Building findBuildingById(BuildingId buildingId);

  Building getParentBuilding(ZoneId zoneId);

  void saveBuildingState(Building changedBuilding);

  void clear();
}
