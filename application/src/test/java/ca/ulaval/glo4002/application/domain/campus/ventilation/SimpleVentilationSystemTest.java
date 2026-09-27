package ca.ulaval.glo4002.application.domain.campus.ventilation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationState;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationSystem;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SimpleVentilationSystemTest{

  private final BuildingId BUILDING_ID = new BuildingId("PLT");
  private final ZoneId ZONE_ID = new ZoneId("THIS IS A VALID ZONEID");
  private final SimpleVentilationState CLOSED_STATE = SimpleVentilationState.CLOSED;
  private final int MEDIAN_SPEED = 50;
  private final int PRESSURE_VARIATION = 10;
  private SimpleVentilationSystem system;
  private ActionBuilder actionBuilder;

  @BeforeEach
  void createSystem(){
    actionBuilder = mock(ActionBuilder.class);
    when(actionBuilder.createOpenCloseVentilationAction(any(BuildingId.class),any(ZoneId.class),
        anyInt())).thenReturn(mock(Action.class));
    system = new SimpleVentilationSystem();
  }

  @Test
  void givenInitialState_whenConstructingSystem_thenSystemLoadsState(){
    Map<String, String> initial = Map.of("isOpen",CLOSED_STATE.toString());

    SimpleVentilationSystem initializedSystem = new SimpleVentilationSystem(initial);

    assertEquals(CLOSED_STATE.toString(),initializedSystem.getCurrentState().get("isOpen"));
  }

  @Test
  void givenClosedStateRequired_whenAdjustVentilation_thenInternalStateBecomesClosed(){
    system.adjustVentilation(BUILDING_ID,ZONE_ID,MEDIAN_SPEED,PRESSURE_VARIATION,CLOSED_STATE,
        actionBuilder);

    assertEquals(CLOSED_STATE.toString(),system.getCurrentState().get("isOpen"));
  }

  @Test
  void givenOpenCloseVentilationCalled_whenInvoked_thenActionBuilderIsCalledWithCorrectArguments(){
    system.adjustVentilation(BUILDING_ID,ZONE_ID,MEDIAN_SPEED,PRESSURE_VARIATION,CLOSED_STATE,
        actionBuilder);

    verify(actionBuilder).createOpenCloseVentilationAction(eq(BUILDING_ID),eq(ZONE_ID),
        eq(CLOSED_STATE.toInt()));
  }

  @Test
  void givenOpenCloseVentilationCalled_whenInvoked_thenReturnsActionFromActionBuilder(){
    Action expectedAction = mock(Action.class);
    when(actionBuilder.createOpenCloseVentilationAction(BUILDING_ID,ZONE_ID,0))
        .thenReturn(expectedAction);

    Action returned = system.adjustVentilation(BUILDING_ID,ZONE_ID,MEDIAN_SPEED,PRESSURE_VARIATION,
        CLOSED_STATE,actionBuilder);

    assertSame(expectedAction,returned);
  }

  @Test
  void whenHasExtremeNegativePressure_thenReturnsFalse(){
    assertFalse(system.hasExtremeNegativePressure());
  }
}
