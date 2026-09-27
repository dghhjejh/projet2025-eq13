package ca.ulaval.glo4002.application.infrastructure.repositories.acces;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import ca.ulaval.glo4002.application.domain.access.AccessCardRepository;
import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.factories.AccessCardRepositoryFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccessCardRepositoryFactoryTest{
  private final String SQLITE_DB_URL = "jdbc:sqlite::memory:";
  private ApplicationConfig appConfig;
  private AccessCardRepositoryFactory accessCardRepositoryFactory;

  @BeforeEach
  public void createAppConfigAndAccessCardRepositoryFactory(){
    appConfig = mock(ApplicationConfig.class);
    accessCardRepositoryFactory = new AccessCardRepositoryFactory(appConfig);
  }

  @Test
  void givenDbTypeIsSqlite_whenCreate_thenReturnSQLiteRepository(){
    String SQLITE_DB_TYPE = "sqlite";
    when(appConfig.getPersistenceType()).thenReturn(SQLITE_DB_TYPE);
    when(appConfig.getAccessCardDbUrl()).thenReturn(SQLITE_DB_URL);
    when(appConfig.getAccessCardDbInitScript()).thenReturn("");

    AccessCardRepository accessCardRepository = accessCardRepositoryFactory.create();

    assertInstanceOf(AccessCardRepository.class,accessCardRepository);
  }

  @Test
  void givenDbTypeIsUnsupported_whenCreate_thenThrowException(){
    String UNSUPPORTED_DB_TYPE = "unsupported_db";
    when(appConfig.getPersistenceType()).thenReturn(UNSUPPORTED_DB_TYPE);
    when(appConfig.getUserDbUrl()).thenReturn(SQLITE_DB_URL);
    when(appConfig.getUserDbInitScript()).thenReturn("");

    assertThrows(IllegalArgumentException.class,accessCardRepositoryFactory::create);
  }
}
