package ca.ulaval.glo4002.application.infrastructure.database;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider{
  private final String dbUrl;
  private final String initScriptPath;

  public ConnectionProvider(String dbUrl,String initScriptPath) {
    this.dbUrl = dbUrl;
    this.initScriptPath = initScriptPath;
  }

  public Connection getConnection(){
    try{
      Connection connection = DriverManager.getConnection(this.dbUrl);

      if (this.initScriptPath != null && !this.initScriptPath.isEmpty()){
        DatabaseInitializer.initializeFromFile(connection,this.initScriptPath);
      }

      return connection;
    } catch (SQLException e){
      throw new RuntimeException("Could not connect to database.",e);
    } catch (IOException e){
      throw new RuntimeException("Could not initialize the database schema.",e);
    }
  }
}
