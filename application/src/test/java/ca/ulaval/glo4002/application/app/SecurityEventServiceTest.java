package ca.ulaval.glo4002.application.app;

import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.event.SecurityEvent;
import ca.ulaval.glo4002.application.domain.event.SecurityEventName;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SecurityEventServiceTest{
  private static final SecurityEvent SECURITY_EVENT = new SecurityEvent(
      SecurityEventName.CANCELLED_PREALARM,new ZoneId("PLT200"),LocalTime.NOON,new ArrayList<>());
  private SecurityEventService securityEventService;
  private BuildingRepository buildingRepository;
  private Building building;
  private ActionBuilder actionBuilder;
  private UserRepository userRepository;

  @BeforeEach
  public void setUp(){
    userRepository = mock(UserRepository.class);
    actionBuilder = mock(ActionBuilder.class);
    buildingRepository = mock(BuildingRepository.class);
    building = mock(Building.class);
    when(buildingRepository.getParentBuilding(SECURITY_EVENT.zoneId())).thenReturn(building);
    when(building.handleEvent(SECURITY_EVENT.zoneId(),SECURITY_EVENT.name(),
        SECURITY_EVENT.parameters(),actionBuilder,userRepository))
        .thenReturn(List.of(mock(Action.class)));
    securityEventService = new SecurityEventService(buildingRepository,actionBuilder,
        userRepository);
  }

  @Test
  public void givenSecurityEvent_whenProcessEvent_thenShouldGetParentBuilding(){
    securityEventService.processEvent(SECURITY_EVENT);

    verify(buildingRepository).getParentBuilding(SECURITY_EVENT.zoneId());
  }

  @Test
  public void givenSecurityEvent_whenProcessEvent_thenShouldHandleEventInBuilding(){
    securityEventService.processEvent(SECURITY_EVENT);

    verify(building).handleEvent(SECURITY_EVENT.zoneId(),SECURITY_EVENT.name(),
        SECURITY_EVENT.parameters(),actionBuilder,userRepository);
  }

  @Test
  public void givenSecurityEvent_whenProcessEvent_thenShouldSaveBuildingState(){
    securityEventService.processEvent(SECURITY_EVENT);

    verify(buildingRepository).saveBuildingState(building);
  }
}
