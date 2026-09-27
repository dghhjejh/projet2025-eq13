package ca.ulaval.glo4002.application.domain.gathering;

import static java.util.UUID.randomUUID;

public class GatheringId{
  private final String gatheringId;

  public GatheringId(String number) {
    this.gatheringId = number;
  }

  public GatheringId() {
    this.gatheringId = randomUUID().toString();
  }

  @Override
  public int hashCode(){
    return this.gatheringId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    GatheringId that = (GatheringId) o;
    return this.gatheringId.equals(that.gatheringId);
  }

  @Override
  public String toString(){
    return this.gatheringId;
  }
}
