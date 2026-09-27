package ca.ulaval.glo4002.application.domain.campus.ventilation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.actions.AdjustVentilationSpeed;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.domain.campus.id.ZoneId;
import ca.ulaval.glo4002.application.domain.ventilation.AdjustablePressureVentilationSystem;
import ca.ulaval.glo4002.application.domain.ventilation.SimpleVentilationState;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdjustableVentilationSystemTest{
  private final BuildingId BUILDING_ID = new BuildingId("PLT");
  private final ZoneId ZONE_ID = new ZoneId("THIS IS A ZONE ID");
  private final SimpleVentilationState CLOSED_STATE = SimpleVentilationState.CLOSED;
  private ActionBuilder actionBuilder;
  private final int MEDIAN = 50;
  private final int VARIATION = -10;
  AdjustablePressureVentilationSystem system;

  @BeforeEach()
  void createSystem(){
    actionBuilder = mock(ActionBuilder.class);
    system = new AdjustablePressureVentilationSystem();
  }

  @Test
  void givenInitialState_whenConstructing_thenInternalStateIsLoaded(){
    Map<String, String> initial = Map.of("distributionSpeed","60","returnSpeed","40");

    AdjustablePressureVentilationSystem system = new AdjustablePressureVentilationSystem(initial);

    Map<String, String> state = system.getCurrentState();

    assertEquals("60",state.get("distributionSpeed"));
    assertEquals("40",state.get("returnSpeed"));
  }

  @Test
  void givenMedianAndVariation_whenAdjustVentilation_thenInternalSpeedsAreUpdated(){

    system.adjustVentilation(BUILDING_ID,ZONE_ID,MEDIAN,VARIATION,CLOSED_STATE,actionBuilder);

    Map<String, String> state = system.getCurrentState();

    int expectedDistribution = MEDIAN + VARIATION;
    int expectedReturn = 2 * MEDIAN - expectedDistribution;

    assertEquals(Integer.toString(expectedDistribution),state.get("distributionSpeed"));
    assertEquals(Integer.toString(expectedReturn),state.get("returnSpeed"));
  }

  @Test
  void givenValues_whenAdjustVentilation_thenReturnedActionContainsCorrectSpeeds(){

    int expectedDistribution = MEDIAN + VARIATION;
    int expectedReturn = 2 * MEDIAN - expectedDistribution;

    AdjustVentilationSpeed expected = new AdjustVentilationSpeed(BUILDING_ID,ZONE_ID,
        expectedDistribution,expectedReturn);

    when(actionBuilder.createAdjustVentilationSpeedAction(BUILDING_ID,ZONE_ID,expectedDistribution,
        expectedReturn)).thenReturn(mock(Action.class));

    Action result = system.adjustVentilation(BUILDING_ID,ZONE_ID,MEDIAN,VARIATION,CLOSED_STATE,
        actionBuilder);

    assertEquals(expected,result);
  }

  @Test
  void givenSpeedsDifferenceLessOrEqualToMinus60_whenHasExtremeNegativePressure_thenReturnsTrue(){
    AdjustablePressureVentilationSystem system = new AdjustablePressureVentilationSystem(
        Map.of("distributionSpeed","10","returnSpeed","80"));

    assertTrue(system.hasExtremeNegativePressure());
  }

  @Test
  void givenSpeedsDifferenceGreaterThanMinus60_whenHasExtremeNegativePressure_thenReturnsFalse(){
    AdjustablePressureVentilationSystem system = new AdjustablePressureVentilationSystem(
        Map.of("distributionSpeed","60","returnSpeed","40"));

    assertFalse(system.hasExtremeNegativePressure());
  }
}
