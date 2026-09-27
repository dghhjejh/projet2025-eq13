package ca.ulaval.glo4002.application.app;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.AccessCardRepository;
import ca.ulaval.glo4002.application.domain.access.AccessRequestEvent;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import jakarta.inject.Inject;

public class AccessRequestService{

  private final BuildingRepository buildingRepository;
  private final AccessCardRepository accessCardRepository;

  @Inject
  public AccessRequestService(BuildingRepository buildingRepository,
      AccessCardRepository accessCardRepository) {
    this.buildingRepository = buildingRepository;
    this.accessCardRepository = accessCardRepository;
  }

  public AccessStatus processAccessRequest(AccessRequestEvent accessRequestEvent){
    AccessCard accessCard = this.accessCardRepository
        .getAccessCardByCardId(accessRequestEvent.cardId());
    Building building = this.buildingRepository.getParentBuilding(accessRequestEvent.zoneId());
    AccessStatus accessStatus = building.evaluateAccessTo(accessCard,accessRequestEvent.zoneId(),
        accessRequestEvent.doorId(),accessRequestEvent.roomId());

    this.buildingRepository.saveBuildingState(building);

    return accessStatus;
  }
}
