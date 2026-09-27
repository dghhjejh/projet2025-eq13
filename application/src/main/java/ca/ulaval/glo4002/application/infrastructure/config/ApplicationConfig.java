package ca.ulaval.glo4002.application.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ApplicationConfig{

  private static final String CONFIG_FILE = "application.properties";
  private static final String PERSISTENCE_SYSTEM_PROPERTY = "persistence";
  private final Properties properties;

  public ApplicationConfig() {
    this.properties = this.loadProperties();
  }

  private Properties loadProperties(){
    Properties properties = new Properties();
    try (InputStream input = getClass().getClassLoader().getResourceAsStream(CONFIG_FILE)){
      if (input == null){
        throw new RuntimeException("Unable to find " + CONFIG_FILE);
      }
      properties.load(input);
    } catch (IOException e){
      throw new RuntimeException("Error loading configuration",e);
    }
    return properties;
  }

  public int getServerPort(){
    return Integer.parseInt(getProperty("server.port","8181"));
  }

  private String getProperty(String key,String defaultValue){
    String value = getProperty(key);
    return value.isEmpty() ? defaultValue : value;
  }

  private String getProperty(String key){
    String value = System.getenv(key.toUpperCase().replace(".","_"));
    if (value != null){
      return value;
    }
    return this.properties.getProperty(key,"");
  }

  public String getPersistenceType(){
    String persistenceType = getPersistenceSystemProperty();
    if (persistenceType != null && !persistenceType.isEmpty()){
      return persistenceType;
    }
    return getProperty("persistence.type");
  }

  private String getPersistenceSystemProperty(){
    return System.getProperty(ApplicationConfig.PERSISTENCE_SYSTEM_PROPERTY);
  }

  public String getUserDbUrl(){
    String configuredPath = getProperty("user.db.url");

    if (configuredPath.equals(":memory:")){
      return "jdbc:sqlite::memory:";
    }

    return buildPortableDbPath(configuredPath);
  }

  public String getAccessCardDbUrl(){
    String configuredPath = getProperty("accesscard.db.url");

    if (configuredPath.equals(":memory:")){
      return "jdbc:sqlite::memory:";
    }

    return buildPortableDbPath(configuredPath);
  }

  private String buildPortableDbPath(String relativePath){
    String tempDir = System.getProperty("java.io.tmpdir");
    Path dbPath = Paths.get(tempDir,"data",relativePath);

    try{
      Files.createDirectories(dbPath.getParent());
    } catch (IOException e){
      throw new RuntimeException("Could not create database directories",e);
    }

    return "jdbc:sqlite:" + dbPath;
  }

  public String getUserDbInitScript(){
    return getProperty("user.db.init.script","");
  }

  public String getCampusDbUrl(){
    String configuredPath = getProperty("campus.db.url");

    if (configuredPath.equals(":memory:")){
      return "jdbc:sqlite::memory:";
    }

    return buildPortableDbPath(configuredPath);
  }

  public String getCampusDbInitScript(){
    return getProperty("campus.db.init.script","");
  }

  public String getAccessCardDbInitScript(){
    return getProperty("accesscard.db.init.script","");
  }

  public String getCampusApiUrl(){
    return getProperty("campus.api.url");
  }
}
