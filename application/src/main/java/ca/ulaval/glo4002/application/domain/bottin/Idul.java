package ca.ulaval.glo4002.application.domain.bottin;

public class Idul{
  private final String idul;

  public Idul(String number) {
    this.idul = number;
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    Idul that = (Idul) o;
    return this.idul.equals(that.idul);
  }

  @Override
  public int hashCode(){
    return this.idul.hashCode();
  }

  @Override
  public String toString(){
    return this.idul;
  }

  public boolean isNull(){
    return idul == null;
  }
}
