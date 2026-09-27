package ca.ulaval.glo4002.application.infrastructure.factories;

import ca.ulaval.glo4002.application.domain.access.AccessCardRepository;
import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.database.ConnectionProvider;
import ca.ulaval.glo4002.application.infrastructure.repositories.AccessCardSQLite;

public class AccessCardRepositoryFactory{
  private final ApplicationConfig config;

  public AccessCardRepositoryFactory(ApplicationConfig config) {
    this.config = config;
  }

  public AccessCardRepository create(){
    String accessCardType = this.config.getPersistenceType();

    return switch (accessCardType.toLowerCase()){
      case "sqlite", "memory" -> {
        ConnectionProvider dbProvider = new ConnectionProvider(this.config.getAccessCardDbUrl(),
            this.config.getAccessCardDbInitScript());
        yield new AccessCardSQLite(dbProvider.getConnection());
      }
      default ->
        throw new IllegalArgumentException("Unsupported AccessCard type: " + accessCardType);
    };
  }
}
