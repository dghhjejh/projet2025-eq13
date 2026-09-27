package ca.ulaval.glo4002.application.infrastructure.repositories;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.AccessCardRepository;
import ca.ulaval.glo4002.application.domain.access.SecurityRole;
import ca.ulaval.glo4002.application.domain.access.id.CardId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AccessCardSQLite implements AccessCardRepository{
  private final Connection connection;

  public AccessCardSQLite(Connection connection) {
    this.connection = connection;
  }

  @Override
  public AccessCard getAccessCardByCardId(CardId cardId){
    if (cardId == null){
      return null;
    }
    String cardIdString = cardId.toString();
    try (PreparedStatement preparedStatement = this.connection
        .prepareStatement("SELECT idul, role_securite FROM cartes_acces WHERE id_carte = ?")){
      preparedStatement.setString(1,cardIdString);
      ResultSet resultSet = preparedStatement.executeQuery();
      resultSet.next();
      Idul idul = new Idul(resultSet.getString("idul"));
      SecurityRole securityRole = SecurityRole.fromString(resultSet.getString("role_securite"));
      return new AccessCard(idul,securityRole);
    } catch (SQLException e){
      throw new RuntimeException(
          "Database error while retrieving access card for card ID: " + cardIdString,e);
    }
  }
}
