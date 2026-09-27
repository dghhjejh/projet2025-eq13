package ca.ulaval.glo4002.application.interfaces.rest.resources;

import static org.junit.jupiter.api.Assertions.assertEquals;

import ca.ulaval.glo4002.application.app.SecurityEventService;
import ca.ulaval.glo4002.application.domain.bottin.UserRepository;
import ca.ulaval.glo4002.application.domain.builders.ActionBuilder;
import ca.ulaval.glo4002.application.domain.campus.BuildingRepository;
import ca.ulaval.glo4002.application.infrastructure.api.CampusAPIClient;
import ca.ulaval.glo4002.application.infrastructure.api.CampusLoaderFromAPI;
import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.factories.UserRepositoryFactory;
import ca.ulaval.glo4002.application.infrastructure.mappers.CampusMapper;
import ca.ulaval.glo4002.application.infrastructure.repositories.BuildingInMemory;
import ca.ulaval.glo4002.application.interfaces.rest.exceptionMappers.*;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.ActionMapper;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.SecurityEventMapper;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.net.URI;
import java.net.URISyntaxException;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.jupiter.api.Test;

class SecurityEventResourceEnd2EndTest extends JerseyTest{

  private final SecurityEventResourceEnd2EndFixture fixture = new SecurityEventResourceEnd2EndFixture();

  private static SecurityEventService getSecurityEventService(){
    ApplicationConfig config = new ApplicationConfig();
    CampusAPIClient campusAPIClient;
    try{
      campusAPIClient = new CampusAPIClient(new URI(config.getCampusApiUrl()));
    } catch (URISyntaxException e){
      throw new RuntimeException(e);
    }

    CampusMapper campusMapper = new CampusMapper();
    ActionBuilder actionBuilder = new ActionBuilder();
    CampusLoaderFromAPI campusLoader = new CampusLoaderFromAPI(campusAPIClient,campusMapper);
    BuildingRepository buildingRepository = new BuildingInMemory(campusLoader);
    UserRepositoryFactory userRepositoryFactory = new UserRepositoryFactory(config);
    UserRepository userRepository = userRepositoryFactory.create();

    return new SecurityEventService(buildingRepository,actionBuilder,userRepository);
  }

  @Override
  protected Application configure(){
    ActionMapper actionMapper = new ActionMapper();
    SecurityEventMapper securityEventMapper = new SecurityEventMapper();
    SecurityEventService securityEventService = getSecurityEventService();

    return new ResourceConfig()
        .register(new SecurityEventResource(securityEventService,actionMapper,securityEventMapper))
        .register(new IllegalArgumentExceptionMapper()).register(new CatchallExceptionMapper())
        .register(new ConstraintValidationExceptionMapper())
        .register(new InvalidFormatExceptionMapper()).register(NotFoundExceptionMapper.class)
        .register(CustomConstraintValidationExceptionMapper.class);
  }

  @Test
  void givenValidFirePrealarmEvent_whenCreatingSecurityEvent_thenReturns200(){
    try (Response response = target("/evenement-securite").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.validFirePrealarmEventJson()))){

      assertEquals(200,response.getStatus());
    }
  }

  @Test
  void givenInvalidName_whenCreatingSecurityEvent_thenReturns400(){
    try (Response response = target("/evenement-securite").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.invalidNameEventJson()))){

      assertEquals(400,response.getStatus());
    }
  }

  @Test
  void givenInvalidTime_whenCreatingSecurityEvent_thenReturns400(){
    try (Response response = target("/evenement-securite").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.invalidTimeEventJson()))){

      assertEquals(400,response.getStatus());
    }
  }

  @Test
  void givenNullEventName_whenCreatingSecurityEvent_thenReturns400(){
    try (Response response = target("/evenement-securite").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.nullEventNameJson()))){

      assertEquals(400,response.getStatus());
    }
  }

  @Test
  void givenInvalidParameters_whenCreatingSecurityEvent_thenReturns400(){
    try (Response response = target("/evenement-securite").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.invalidParametersEventJson()))){

      assertEquals(400,response.getStatus());
    }
  }

  @Test
  void givenSmokePresenceNoConcentration_whenCreatingSecurityEvent_thenReturns400(){
    try (Response response = target("/evenement-securite").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.smokePresenceNoConcentrationEventJson()))){

      assertEquals(400,response.getStatus());
    }
  }

  @Test
  void givenSmokePresenceNegativeConcentration_whenCreatingSecurityEvent_thenReturns400(){
    try (Response response = target("/evenement-securite").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(fixture.smokePresenceNegativeConcentrationEventJson()))){

      assertEquals(400,response.getStatus());
    }
  }
}
