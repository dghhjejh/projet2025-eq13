package ca.ulaval.glo4002.application.domain.campus.id;

public class RoomId{
  private final String roomId;

  public RoomId(String number) {
    this.roomId = number;
  }

  @Override
  public int hashCode(){
    return this.roomId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    RoomId that = (RoomId) o;
    return this.roomId.equals(that.roomId);
  }

  @Override
  public String toString(){
    return this.roomId;
  }
}
