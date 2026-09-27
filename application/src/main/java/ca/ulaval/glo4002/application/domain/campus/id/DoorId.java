package ca.ulaval.glo4002.application.domain.campus.id;

public class DoorId{
  private final String doorId;

  public DoorId(String number) {
    this.doorId = number;
  }

  @Override
  public int hashCode(){
    return this.doorId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    DoorId that = (DoorId) o;
    return this.doorId.equals(that.doorId);
  }

  @Override
  public String toString(){
    return this.doorId;
  }
}
