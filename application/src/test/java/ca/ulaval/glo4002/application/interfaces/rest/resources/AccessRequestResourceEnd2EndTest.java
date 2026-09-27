package ca.ulaval.glo4002.application.interfaces.rest.resources;

import static org.junit.jupiter.api.Assertions.*;

import ca.ulaval.glo4002.application.app.AccessRequestService;
import ca.ulaval.glo4002.application.domain.access.AccessCardRepository;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.infrastructure.api.CampusAPIClient;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.factories.AccessCardRepositoryFactory;
import ca.ulaval.glo4002.application.infrastructure.mappers.CampusMapper;
import ca.ulaval.glo4002.application.infrastructure.repositories.BuildingInMemory;
import ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers.*;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.AccessRequestMapper;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

class AccessRequestResourceEnd2EndTest extends JerseyTest{

  private final AccessRequestResourceEnd2EndFixture fixture = new AccessRequestResourceEnd2EndFixture();

  @Override
  protected Application configure(){
    AccessRequestMapper accessRequestMapper = new AccessRequestMapper();

    AccessRequestService accessRequestService = getAccessRequestService();

    return new ResourceConfig()
        .register(new AccessRequestResource(accessRequestService,accessRequestMapper))
        .register(new IllegalArgumentExceptionMapper()).register(new CatchallExceptionMapper())
        .register(new ConstraintValidationExceptionMapper())
        .register(new InvalidFormatExceptionMapper()).register(NotFoundExceptionMapper.class)
        .register(CustomConstraintValidationExceptionMapper.class);
  }

  private static AccessRequestService getAccessRequestService(){
    ApplicationConfig config = new ApplicationConfig();
    CampusAPIClient campusAPIClient;
    URI campusApiUri;
    try{
      campusApiUri = new URI(config.getCampusApiUrl());
    } catch (URISyntaxException e){
      throw new RuntimeException(e);
    }
    campusAPIClient = new CampusAPIClient(campusApiUri);
    CampusMapper campusMapper = new CampusMapper();
    CampusLoaderFromAPI campusLoader = new CampusLoaderFromAPI(campusAPIClient,campusMapper);
    AccessCardRepositoryFactory accessCardRepositoryFactory = new AccessCardRepositoryFactory(
        config);
    BuildingRepository buildingRepository = new BuildingInMemory(campusLoader);
    AccessCardRepository accessCardRepository = accessCardRepositoryFactory.create();

    return new AccessRequestService(buildingRepository,accessCardRepository);
  }

  @Test
  void givenNoFire_whenAccessRequestBySecurityAgent_thenReturns200(){
    try (Response response = target("/demande-acces").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.validAccessRequestJson()))){

      assertEquals(200,response.getStatus());
    }
  }

  @Test
  void whenAccessRequestWithNoZoneId_thenReturns400(){
    try (Response response = target("/demande-acces").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.missingZoneIdJson()))){

      assertEquals(400,response.getStatus());
    }
  }
}
