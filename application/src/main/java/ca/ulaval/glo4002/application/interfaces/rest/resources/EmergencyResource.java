package ca.ulaval.glo4002.application.interfaces.rest.resources;

import ca.ulaval.glo4002.application.app.SecurityEventService;
import ca.ulaval.glo4002.application.domain.campus.buildings.Building;
import ca.ulaval.glo4002.application.domain.campus.id.BuildingId;
import ca.ulaval.glo4002.application.infrastructure.exceptions.BuildingNotFoundException;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.BuildingEmergencyStateMapper;
import ca.ulaval.glo4002.application.interfaces.rest.responses.BuildingEmergencyStateResponse;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/v2/emergencies/0/")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class EmergencyResource{
  private final SecurityEventService securityEventService;

  public EmergencyResource(SecurityEventService securityEventService) {
    this.securityEventService = securityEventService;
  }

  @Path("buildings-state/{building-id}")
  @GET
  public Response getBuildingEmergencyState(@PathParam("building-id") String buildingId){
    Building building = securityEventService.getBuilding(new BuildingId(buildingId));
    if (building == null){
      throw new BuildingNotFoundException("There is no building with id: " + buildingId);
    }
    BuildingEmergencyStateResponse buildingEmergencyState = BuildingEmergencyStateMapper
        .toResponse(building);
    return Response.status(200).entity(buildingEmergencyState).build();
  }
}
