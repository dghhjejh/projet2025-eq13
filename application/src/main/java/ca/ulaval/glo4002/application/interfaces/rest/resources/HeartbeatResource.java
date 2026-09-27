package ca.ulaval.glo4002.application.interfaces.rest.resources;

import ca.ulaval.glo4002.application.interfaces.rest.HeartbeatResponse;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/heartbeat")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class HeartbeatResource{
  private final Logger logger = LoggerFactory.getLogger(HeartbeatResource.class);

  @GET
  public HeartbeatResponse heartbeat(@QueryParam("token") @NotNull String token){
    this.logger.info("Received heartbeat : {}",token);
    return new HeartbeatResponse(token);
  }
}
