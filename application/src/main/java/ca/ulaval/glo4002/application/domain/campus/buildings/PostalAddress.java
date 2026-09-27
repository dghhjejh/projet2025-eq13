package ca.ulaval.glo4002.application.domain.campus.buildings;

public class PostalAddress{
  private final String address;

  public PostalAddress(String address) {
    this.address = address;
  }

  @Override
  public int hashCode(){
    return this.address.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PostalAddress that = (PostalAddress) o;
    return this.address.equals(that.address);
  }

  @Override
  public String toString(){
    return this.address;
  }
}
