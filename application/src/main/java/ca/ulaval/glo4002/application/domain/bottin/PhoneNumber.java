package ca.ulaval.glo4002.application.domain.bottin;

public class PhoneNumber{
  private final String number;

  public PhoneNumber(String number) {
    if (number == null){
      throw new IllegalArgumentException("Phone number cannot be null");
    }
    this.number = number;
  }

  @Override
  public int hashCode(){
    return this.number.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    PhoneNumber that = (PhoneNumber) o;
    return this.number.equals(that.number);
  }

  @Override
  public String toString(){
    return this.number;
  }
}
