package ca.ulaval.glo4002.application.domain.campus.id;

public class ZoneId{
  private final String zoneId;

  public ZoneId(String number) {
    this.zoneId = number;
  }

  @Override
  public int hashCode(){
    return this.zoneId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    ZoneId that = (ZoneId) o;
    return this.zoneId.equals(that.zoneId);
  }

  @Override
  public String toString(){
    return this.zoneId;
  }
}
