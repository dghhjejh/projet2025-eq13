package ca.ulaval.glo4002.application.infrastructure.factories;

import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.database.ConnectionProvider;
import ca.ulaval.glo4002.application.infrastructure.repositories.BuildingInMemory;
import ca.ulaval.glo4002.application.infrastructure.repositories.BuildingSQLite;

public class BuildingRepositoryFactory{
  private final ApplicationConfig config;
  private final CampusLoaderFromAPI campusLoader;

  public BuildingRepositoryFactory(ApplicationConfig config,CampusLoaderFromAPI campusLoader) {
    this.config = config;
    this.campusLoader = campusLoader;
  }

  public BuildingRepository create(){
    String campusType = this.config.getPersistenceType();

    return switch (campusType.toLowerCase()){
      case "sqlite" -> {
        ConnectionProvider dbProvider = new ConnectionProvider(this.config.getCampusDbUrl(),
            this.config.getCampusDbInitScript());
        yield new BuildingSQLite(dbProvider.getConnection(),campusLoader);
      }
      case "memory" -> new BuildingInMemory(campusLoader);
      default ->
        throw new IllegalArgumentException("Unsupported buildingRepository type: " + campusType);
    };
  }
}
