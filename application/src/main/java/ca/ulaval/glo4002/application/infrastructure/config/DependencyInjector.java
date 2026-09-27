package ca.ulaval.glo4002.application.infrastructure.config;

import ca.ulaval.glo4002.application.app.AccessRequestService;
import ca.ulaval.glo4002.application.app.SecurityEventService;
import ca.ulaval.glo4002.application.app.SystemReinitializationService;
import ca.ulaval.glo4002.application.domain.access.AccessCardRepository;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.infrastructure.api.CampusAPIClient;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import ca.ulaval.glo4002.application.infrastructure.factories.AccessCardRepositoryFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.BuildingRepositoryFactory;
import ca.ulaval.glo4002.application.infrastructure.factories.UserRepositoryFactory;
import ca.ulaval.glo4002.application.infrastructure.mappers.CampusMapper;
import ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers.*;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.AccessRequestMapper;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.ActionMapper;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.SecurityEventMapper;
import ca.ulaval.glo4002.application.interfaces.rest.resources.AccessRequestResource;
import ca.ulaval.glo4002.application.interfaces.rest.resources.EmergencyResource;
import ca.ulaval.glo4002.application.interfaces.rest.resources.ResetBuildingMapResource;
import ca.ulaval.glo4002.application.interfaces.rest.resources.SecurityEventResource;
import java.net.URI;
import java.net.URISyntaxException;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class DependencyInjector{

  private final ApplicationConfig config;
  private final BuildingRepository buildingRepository;
  private final SecurityEventService securityEventService;
  private final AccessRequestService accessRequestService;
  private final AccessCardRepository accessCardRepository;
  private final SystemReinitializationService systemReinitializationService;
  private final UserRepository userRepository;

  public DependencyInjector(ApplicationConfig config) {
    this.config = config;

    UserRepositoryFactory userRepositoryFactory = new UserRepositoryFactory(this.config);
    AccessCardRepositoryFactory accessCardRepositoryFactory = new AccessCardRepositoryFactory(
        this.config);

    CampusLoaderFromAPI campusLoader = createCampusLoader();
    BuildingRepositoryFactory buildingRepositoryFactory = new BuildingRepositoryFactory(config,
        campusLoader);

    this.buildingRepository = buildingRepositoryFactory.create();
    this.userRepository = userRepositoryFactory.create();
    this.accessCardRepository = accessCardRepositoryFactory.create();

    this.systemReinitializationService = createSystemReinitializationService();
    this.securityEventService = createSecurityEventService();
    this.accessRequestService = createAccessRequestService();
  }

  private CampusLoaderFromAPI createCampusLoader(){
    URI campusUri;
    try{
      campusUri = new URI(config.getCampusApiUrl());
    } catch (URISyntaxException e){
      throw new RuntimeException(e);
    }
    CampusAPIClient campusAPIClient = new CampusAPIClient(campusUri);
    CampusMapper campusMapper = new CampusMapper();
    return new CampusLoaderFromAPI(campusAPIClient,campusMapper);
  }

  private SystemReinitializationService createSystemReinitializationService(){
    return new SystemReinitializationService(buildingRepository);
  }

  private SecurityEventService createSecurityEventService(){
    return new SecurityEventService(buildingRepository,new ActionBuilder(),userRepository);
  }

  private AccessRequestService createAccessRequestService(){
    return new AccessRequestService(buildingRepository,accessCardRepository);
  }

  public ResourceConfig createResourceConfig(){
    return new ResourceConfig().packages("ca.ulaval.glo4002.application")
        .register(JacksonFeature.withoutExceptionMappers())
        .register(IllegalArgumentExceptionMapper.class).register(CatchallExceptionMapper.class)
        .register(ConstraintValidationExceptionMapper.class)
        .register(InvalidFormatExceptionMapper.class).register(NotFoundExceptionMapper.class)
        .register(CustomConstraintValidationExceptionMapper.class)
        .register(createResetBuildingMapResource()).register(createEmergencyResource())
        .register(createSecurityEventResource()).register(createAccessRequestResource());
  }

  private ResetBuildingMapResource createResetBuildingMapResource(){
    return new ResetBuildingMapResource(systemReinitializationService);
  }

  private EmergencyResource createEmergencyResource(){
    return new EmergencyResource(securityEventService);
  }

  private SecurityEventResource createSecurityEventResource(){
    return new SecurityEventResource(securityEventService,new ActionMapper(),
        new SecurityEventMapper());
  }

  private AccessRequestResource createAccessRequestResource(){
    return new AccessRequestResource(accessRequestService,new AccessRequestMapper());
  }
}
