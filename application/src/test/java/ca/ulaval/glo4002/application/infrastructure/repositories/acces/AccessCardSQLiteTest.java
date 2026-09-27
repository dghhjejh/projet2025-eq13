package ca.ulaval.glo4002.application.infrastructure.repositories.acces;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import ca.ulaval.glo4002.application.domain.access.AccessCard;
import ca.ulaval.glo4002.application.domain.access.SecurityRole;
import ca.ulaval.glo4002.application.domain.access.id.CardId;
import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.infrastructure.repositories.AccessCardSQLite;
import java.sql.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessCardSQLiteTest{
  private static final CardId CARD_ID = new CardId("12345");
  private static final CardId NULL_CARD_ID = null;
  private static final Idul IDUL = new Idul("jdoe");
  private Connection testConnection;
  private AccessCardSQLite repository;

  @BeforeEach
  void createAccessCardDB() throws SQLException{
    String SQLITE_TEST_URL = "jdbc:sqlite::memory:";
    testConnection = DriverManager.getConnection(SQLITE_TEST_URL);
    createTestSchema();
    repository = new AccessCardSQLite(testConnection);
  }

  private void createTestSchema() throws SQLException{
    try (Statement statement = testConnection.createStatement()){
      statement.execute("CREATE TABLE IF NOT EXISTS cartes_acces (" + "id_carte TEXT PRIMARY KEY, "
          + "idul TEXT NOT NULL, " + "role_securite TEXT" + ")");
    }
  }

  @AfterEach
  void closeConnection() throws SQLException{
    if (testConnection != null && !testConnection.isClosed()){
      testConnection.close();
    }
  }

  private void insertTestAccessCard(String cardId,String idul) throws SQLException{
    try (PreparedStatement statement = testConnection
        .prepareStatement("INSERT INTO cartes_acces (id_carte, idul) VALUES (?, ?)")){
      statement.setString(1,cardId);
      statement.setString(2,idul);
      statement.executeUpdate();
    }
  }

  @Test
  void givenExistingCardId_whenGetAccessCardByCardId_thenReturnTheCorrectAccessCard()
      throws SQLException{
    insertTestAccessCard(CARD_ID.toString(),IDUL.toString());

    AccessCard accessCard = repository.getAccessCardByCardId(CARD_ID);

    assertEquals(IDUL,accessCard.idul());
    assertEquals(SecurityRole.NONE,accessCard.securityRole());
  }

  @Test
  void givenNoCardId_whenGetAccessCardByCardId_thenReturnNull(){
    AccessCard accessCard = repository.getAccessCardByCardId(NULL_CARD_ID);

    assertNull(accessCard);
  }
}
