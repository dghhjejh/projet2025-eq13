package ca.ulaval.glo4002.application.infrastructure.repositories.bottin;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.domain.bottin.Idul;
import ca.ulaval.glo4002.application.domain.bottin.PhoneNumber;
import ca.ulaval.glo4002.application.domain.bottin.Status;
import ca.ulaval.glo4002.application.domain.bottin.User;
import ca.ulaval.glo4002.application.infrastructure.repositories.UserSQLite;
import java.sql.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserSQLiteTest{
  private static final Idul IDUL = new Idul("jdoe");
  private static final String NAME = "John Doe";
  private static final PhoneNumber CONTACT = new PhoneNumber("4181234567");
  private static final Status STATUS = Status.AVAILABLE;

  private static final Idul OTHER_USER_IDUL = new Idul("asmith");
  private static final PhoneNumber OTHER_USER_CONTACT = new PhoneNumber("+15147654321");
  private static final Status OTHER_USER_STATUS = Status.IN_A_MEETING;

  private static final Idul UNKNOWN_IDUL = new Idul("unknown");

  private Connection testConnection;
  private UserSQLite repository;

  @BeforeEach
  void createUserDB() throws SQLException{
    String SQLITE_TEST_URL = "jdbc:sqlite::memory:";
    testConnection = DriverManager.getConnection(SQLITE_TEST_URL);
    createTestSchema();
    repository = new UserSQLite(testConnection);
  }

  private void createTestSchema() throws SQLException{
    try (Statement statement = testConnection.createStatement()){
      statement.execute("CREATE TABLE IF NOT EXISTS Users (" + "idul TEXT PRIMARY KEY, "
          + "name TEXT NOT NULL, " + "contact_no TEXT NOT NULL, " + "status TEXT NOT NULL" + ")");
    }
  }

  @AfterEach
  void closeConnection() throws SQLException{
    if (testConnection != null && !testConnection.isClosed()){
      testConnection.close();
    }
  }

  @Test
  void givenUserExists_whenFindUserByIDUL_thenReturnUser() throws SQLException{
    insertTestUser(IDUL,NAME,CONTACT,STATUS);

    User user = repository.findUserByIDUL(IDUL);

    assertNotNull(user);
    assertEquals(IDUL,user.idul());
    assertEquals(NAME,user.name());
    assertEquals(CONTACT,user.contact_no());
    assertEquals(STATUS,user.status());
  }

  private void insertTestUser(Idul idul,String name,PhoneNumber contact,Status status)
      throws SQLException{
    try (PreparedStatement statement = testConnection.prepareStatement(
        "INSERT INTO Users (idul, name, contact_no, status) VALUES (?, ?, ?, ?)")){
      statement.setString(1,idul.toString());
      statement.setString(2,name);
      statement.setString(3,contact.toString());
      statement.setString(4,status.toString());
      statement.executeUpdate();
    }
  }

  @Test
  void givenUserDoesNotExist_whenFindUserByIDUL_thenThrowException(){
    assertThrows(RuntimeException.class,() -> repository.findUserByIDUL(UNKNOWN_IDUL));
  }

  @Test
  void givenMultipleUsers_whenFindUserByIDUL_thenReturnCorrectUser() throws SQLException{
    insertTestUser(IDUL,NAME,CONTACT,STATUS);
    String OTHER_USER_NAME = "Alice Smith";
    insertTestUser(OTHER_USER_IDUL,OTHER_USER_NAME,OTHER_USER_CONTACT,OTHER_USER_STATUS);

    User user = repository.findUserByIDUL(OTHER_USER_IDUL);

    assertEquals(OTHER_USER_IDUL,user.idul());
    assertEquals(OTHER_USER_NAME,user.name());
  }
}
