package ca.ulaval.glo4002.application.domain.campus.buildings;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class SmokeThresholdTest{

  private final int MAX_SMOKE_CONCENTRATION = 100;
  private final int MIN_SMOKE_CONCENTRATION = 0;

  private int firstThreshold;
  private int secondThreshold;
  private int thirdThreshold;

  @Test
  void givenFirstThresholdSuperiorToSecond_whenConstructingSmokeThreshold_thenThrowsIllegalArgumentException(){
    firstThreshold = 20;
    secondThreshold = 10;
    thirdThreshold = 30;

    assertThrows(IllegalArgumentException.class,
        () -> new SmokeThreshold(firstThreshold,secondThreshold,thirdThreshold));
  }

  @Test
  void givenSecondThresholdSuperiorToThird_whenConstructingSmokeThreshold_thenThrowsIllegalArgumentException(){
    firstThreshold = 10;
    secondThreshold = 30;
    thirdThreshold = 20;

    assertThrows(IllegalArgumentException.class,
        () -> new SmokeThreshold(firstThreshold,secondThreshold,thirdThreshold));
  }

  @Test
  void givenFirstThresholdInferiorToMinSmokeConcentration_whenConstructingSmokeThreshold_thenThrowsIllegalArgumentException(){
    firstThreshold = MIN_SMOKE_CONCENTRATION - 10;
    secondThreshold = 20;
    thirdThreshold = 30;

    assertThrows(IllegalArgumentException.class,
        () -> new SmokeThreshold(firstThreshold,secondThreshold,thirdThreshold));
  }

  @Test
  void givenThirdThresholdInferiorToMaxSmokeConcentration_whenConstructingSmokeThreshold_thenThrowsIllegalArgumentException(){
    firstThreshold = 10;
    secondThreshold = 20;
    thirdThreshold = MAX_SMOKE_CONCENTRATION + 10;

    assertThrows(IllegalArgumentException.class,
        () -> new SmokeThreshold(firstThreshold,secondThreshold,thirdThreshold));
  }

  @Test
  void givenConcentrationSuperiorToFirstThresholdAndInferiorToSecondThreshold_whenIsAtThreshold1_thenReturnsTrue(){
    SmokeThreshold smokeThreshold = new SmokeThreshold(10,20,30);

    assertTrue(smokeThreshold.isAtThreshold1(15));
  }

  @Test
  void givenConcentrationInferiorToFirstThreshold_whenIsAtThreshold1_thenReturnsFalse(){
    SmokeThreshold smokeThreshold = new SmokeThreshold(10,20,30);

    assertFalse(smokeThreshold.isAtThreshold1(5));
  }

  @Test
  void givenConcentrationSuperiorToSecondThreshold_whenIsAtThreshold1_thenReturnsFalse(){
    SmokeThreshold smokeThreshold = new SmokeThreshold(10,20,30);

    assertFalse(smokeThreshold.isAtThreshold1(25));
  }

  @Test
  void givenConcentrationSuperiorToSecondThreshold_whenIsAtOrAboveThreshold2_thenReturnsTrue(){
    SmokeThreshold smokeThreshold = new SmokeThreshold(10,20,30);

    assertTrue(smokeThreshold.isAtOrAboveThreshold2(25));
  }

  @Test
  void givenConcentrationInferiorToSecondThreshold_whenIsAtOrAboveThreshold2_thenReturnsFalse(){
    SmokeThreshold smokeThreshold = new SmokeThreshold(10,20,30);

    assertFalse(smokeThreshold.isAtOrAboveThreshold2(15));
  }
}
