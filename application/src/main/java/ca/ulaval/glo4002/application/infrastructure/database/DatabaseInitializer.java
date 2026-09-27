package ca.ulaval.glo4002.application.infrastructure.database;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.stream.Collectors;

public class DatabaseInitializer{
  public static void initializeFromFile(Connection connection,String resourcePath)
      throws SQLException, IOException{
    try (InputStream inputStream = DatabaseInitializer.class.getResourceAsStream(resourcePath)){
      assert inputStream != null;
      try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
          Statement connectionStatement = connection.createStatement()){

        String sql = reader.lines().collect(Collectors.joining("\n"));
        String[] statements = sql.split(";");

        for (String statement : statements){
          String trimmed = statement.trim();
          if (!trimmed.isEmpty()){
            connectionStatement.execute(trimmed);
          }
        }
      }
    }
  }
}
