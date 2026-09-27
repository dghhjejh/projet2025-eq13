package ca.ulaval.glo4002.application.interfaces.rest.resources;

import ca.ulaval.glo4002.application.app.SystemReinitializationService;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

@Path("/reinitialiser")
public class ResetBuildingMapResource{
  private final SystemReinitializationService systemReinitializationService;

  public ResetBuildingMapResource(SystemReinitializationService systemReinitializationService) {
    this.systemReinitializationService = systemReinitializationService;
  }

  @POST
  public Response reset(){
    this.systemReinitializationService.resetBuildingMap();
    return Response.status(200).build();
  }
}
