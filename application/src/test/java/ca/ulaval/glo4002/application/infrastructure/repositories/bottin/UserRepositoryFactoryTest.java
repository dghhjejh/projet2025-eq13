package ca.ulaval.glo4002.application.infrastructure.repositories.bottin;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.factories.UserRepositoryFactory;
import ca.ulaval.glo4002.application.infrastructure.repositories.UserSQLite;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserRepositoryFactoryTest{
  private final String SQLITE_DB_URL = "jdbc:sqlite::memory:";
  private ApplicationConfig appConfig;
  private UserRepositoryFactory factory;

  @BeforeEach
  public void createAppConfig(){
    appConfig = mock(ApplicationConfig.class);
    factory = new UserRepositoryFactory(appConfig);
  }

  @Test
  void givenDbTypeIsSqlite_whenCreate_thenReturnSQLiteRepository(){
    String SQLITE_DB_TYPE = "sqlite";
    when(appConfig.getPersistenceType()).thenReturn(SQLITE_DB_TYPE);
    when(appConfig.getUserDbUrl()).thenReturn(SQLITE_DB_URL);
    when(appConfig.getUserDbInitScript()).thenReturn("");

    UserRepository userRepository = factory.create();

    assertInstanceOf(UserSQLite.class,userRepository);
  }

  @Test
  void givenDbTypeIsUnsupported_whenCreate_thenThrowException(){
    String UNSUPPORTED_DB_TYPE = "unsupported_db";
    when(appConfig.getPersistenceType()).thenReturn(UNSUPPORTED_DB_TYPE);
    when(appConfig.getUserDbUrl()).thenReturn(SQLITE_DB_URL);
    when(appConfig.getUserDbInitScript()).thenReturn("");

    assertThrows(IllegalArgumentException.class,factory::create);
  }
}
