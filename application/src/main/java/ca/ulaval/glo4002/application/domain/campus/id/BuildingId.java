package ca.ulaval.glo4002.application.domain.campus.id;

public class BuildingId{
  private final String buildingId;

  public BuildingId(String number) {
    this.buildingId = number;
  }

  @Override
  public int hashCode(){
    return this.buildingId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    BuildingId that = (BuildingId) o;
    return this.buildingId.equals(that.buildingId);
  }

  @Override
  public String toString(){
    return this.buildingId;
  }
}
