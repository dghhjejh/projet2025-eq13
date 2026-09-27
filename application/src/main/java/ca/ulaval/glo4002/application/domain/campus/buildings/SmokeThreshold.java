package ca.ulaval.glo4002.application.domain.campus.buildings;

public class SmokeThreshold{

  private final int threshold1;
  private final int threshold2;
  private final int MAX_SMOKE_CONCENTRATION = 100;
  private final int MIN_SMOKE_CONCENTRATION = 0;

  public SmokeThreshold(int threshold1,int threshold2,int threshold3) {
    validateThresholds(threshold1,threshold2,threshold3);
    this.threshold1 = threshold1;
    this.threshold2 = threshold2;
  }

  private void validateThresholds(int firstThreshold,int secondThreshold,int thirdThreshold){
    if (firstThreshold >= secondThreshold || secondThreshold >= thirdThreshold){
      throw new IllegalArgumentException(
          "Thresholds must be in ascending order: threshold1 < threshold2 < threshold3");
    }
    if (firstThreshold < MIN_SMOKE_CONCENTRATION || thirdThreshold > MAX_SMOKE_CONCENTRATION){
      throw new IllegalArgumentException("Thresholds must be between 0 and 100");
    }
  }

  public boolean isAtThreshold1(int concentration){
    return concentration >= this.threshold1 && concentration < this.threshold2;
  }

  public boolean isAtOrAboveThreshold2(int concentration){
    return concentration >= this.threshold2;
  }
}
