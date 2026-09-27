package ca.ulaval.glo4002.application.infrastructure.factories;

import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.database.ConnectionProvider;
import ca.ulaval.glo4002.application.infrastructure.repositories.UserSQLite;

public class UserRepositoryFactory{
  private final ApplicationConfig config;

  public UserRepositoryFactory(ApplicationConfig config) {
    this.config = config;
  }

  public UserRepository create(){
    String userRepoType = this.config.getPersistenceType();

    return switch (userRepoType.toLowerCase()){
      case "sqlite", "memory" -> {
        ConnectionProvider dbProvider = new ConnectionProvider(this.config.getUserDbUrl(),
            this.config.getUserDbInitScript());
        yield new UserSQLite(dbProvider.getConnection());
      }
      default ->
        throw new IllegalArgumentException("Unsupported User Repository type: " + userRepoType);
    };
  }
}
