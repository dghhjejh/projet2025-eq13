package ca.ulaval.glo4002.application.infrastructure.repositories;

import static java.lang.String.valueOf;

import ca.ulaval.glo4002.application.domain.bottin.*;
import ca.ulaval.glo4002.application.infrastructure.exceptions.UserNotFoundException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserSQLite implements UserRepository{

  private final Connection connection;

  public UserSQLite(Connection connection) {
    this.connection = connection;
  }

  public User findUserByIDUL(Idul idul){
    try (PreparedStatement statement = this.connection
        .prepareStatement("SELECT name, contact_no, status FROM Users WHERE idul = ?")){
      statement.setString(1,valueOf(idul));
      ResultSet resultSet = statement.executeQuery();

      if (resultSet.next()){
        String name = resultSet.getString("name");
        PhoneNumber contact = new PhoneNumber(resultSet.getString("contact_no"));
        Status status = Status.valueOf(resultSet.getString("status"));
        return new User(idul,name,contact,status);
      } else{
        throw new UserNotFoundException("Could not find user with idul: " + idul);
      }
    } catch (SQLException e){
      throw new RuntimeException("Could not query database.",e);
    }
  }
}
