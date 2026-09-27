package ca.ulaval.glo4002.application.domain.access;

import ca.ulaval.glo4002.application.domain.access.id.CardId;

public interface AccessCardRepository{
  AccessCard getAccessCardByCardId(CardId cardId);
}
