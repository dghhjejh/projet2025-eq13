package ca.ulaval.glo4002.application.app;

import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.access.*;
import ca.ulaval.glo4002.application.domain.access.id.CardId;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.domain.campus.Campus;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.id.DoorId;
import ca.ulaval.glo4002.application.domain.campus.id.RoomId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import java.time.LocalTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessRequestServiceTest{

  private static final CardId A_CARD_ID = new CardId("Card id");
  private static final ZoneId A_ZONE_ID = new ZoneId("PLT200");
  private static final RoomId A_ROOM_ID = new RoomId("1");
  private static final DoorId A_DOOR_ID = new DoorId("PLT200-COUPE-FEU-01");
  private AccessCard accessCard;
  private static final AccessStatus AN_ACCESS_STATUS = AccessStatus.ALLOWED;
  private static final LocalTime A_TIMESTAMP = LocalTime.NOON;
  private static final AccessRequestEvent ACCESS_REQUEST_EVENT = new AccessRequestEvent(A_CARD_ID,
      A_ZONE_ID,A_ROOM_ID,A_DOOR_ID,A_TIMESTAMP);

  private AccessRequestService accessRequestService;
  private BuildingRepository buildingRepository;
  private AccessCardRepository accessCardRepository;
  private Building building;

  @BeforeEach
  void setUp(){
    CampusLoaderFromAPI campusLoader = mock(CampusLoaderFromAPI.class);
    buildingRepository = mock(BuildingRepository.class);
    accessCardRepository = mock(AccessCardRepository.class);
    Campus campus = mock(Campus.class);
    building = mock(Building.class);
    accessCard = mock(AccessCard.class);

    when(campusLoader.loadCampus()).thenReturn(campus);
    when(buildingRepository.getParentBuilding(ACCESS_REQUEST_EVENT.zoneId())).thenReturn(building);
    when(accessCardRepository.getAccessCardByCardId(A_CARD_ID)).thenReturn(accessCard);
    when(building.evaluateAccessTo(accessCard,A_ZONE_ID,A_DOOR_ID,A_ROOM_ID))
        .thenReturn(AN_ACCESS_STATUS);

    accessRequestService = new AccessRequestService(buildingRepository,accessCardRepository);
  }

  @Test
  void whenProcessAccessRequest_thenShouldGetParentBuilding(){
    accessRequestService.processAccessRequest(ACCESS_REQUEST_EVENT);

    verify(buildingRepository).getParentBuilding(ACCESS_REQUEST_EVENT.zoneId());
  }

  @Test
  void whenProcessAccessRequest_thenShouldGetRoleByCardId(){
    accessRequestService.processAccessRequest(ACCESS_REQUEST_EVENT);

    verify(accessCardRepository).getAccessCardByCardId(A_CARD_ID);
  }

  @Test
  void whenProcessAccessRequest_thenBuildingShouldHandleAccess(){
    accessRequestService.processAccessRequest(ACCESS_REQUEST_EVENT);

    verify(building).evaluateAccessTo(accessCard,A_ZONE_ID,A_DOOR_ID,A_ROOM_ID);
  }

  @Test
  void whenProcessAccessRequest_thenShouldSaveBuildingState(){
    accessRequestService.processAccessRequest(ACCESS_REQUEST_EVENT);

    verify(buildingRepository).saveBuildingState(building);
  }
}
