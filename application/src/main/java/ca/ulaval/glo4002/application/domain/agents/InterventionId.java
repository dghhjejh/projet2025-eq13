package ca.ulaval.glo4002.application.domain.agents;

import java.util.UUID;

public class InterventionId{
  private final UUID interventionId;

  public InterventionId() {
    this.interventionId = UUID.randomUUID();
  }

  public InterventionId(String uuidString) {
    this.interventionId = UUID.fromString(uuidString);
  }

  @Override
  public int hashCode(){
    return this.interventionId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    InterventionId that = (InterventionId) o;
    return this.interventionId.equals(that.interventionId);
  }

  @Override
  public String toString(){
    return this.interventionId.toString();
  }
}
