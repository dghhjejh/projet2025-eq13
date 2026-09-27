package ca.ulaval.glo4002.application.domain.access.id;

public class CardId{

  private final String cardId;

  public CardId(String number) {
    this.cardId = number;
  }

  @Override
  public int hashCode(){
    return this.cardId.hashCode();
  }

  @Override
  public boolean equals(Object o){
    if (this == o)
      return true;
    if (o == null || getClass() != o.getClass())
      return false;
    CardId that = (CardId) o;
    return this.cardId.equals(that.cardId);
  }

  @Override
  public String toString(){
    return this.cardId;
  }
}
