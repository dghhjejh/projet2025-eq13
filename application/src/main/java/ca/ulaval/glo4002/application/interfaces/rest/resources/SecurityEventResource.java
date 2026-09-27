package ca.ulaval.glo4002.application.interfaces.rest.resources;

import ca.ulaval.glo4002.application.app.SecurityEventService;
import ca.ulaval.glo4002.application.domain.actions.Action;
import ca.ulaval.glo4002.application.domain.event.SecurityEvent;
import ca.ulaval.glo4002.application.interfaces.rest.dto.ActionsResponseDTO;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.ActionMapper;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.SecurityEventMapper;
import ca.ulaval.glo4002.application.interfaces.rest.requests.SecurityEventRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/evenement-securite")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SecurityEventResource{

  private final SecurityEventService securityEventService;
  private final ActionMapper actionMapper;
  private final SecurityEventMapper securityEventMapper;

  @Inject
  public SecurityEventResource(SecurityEventService securityEventService,ActionMapper actionMapper,
      SecurityEventMapper securityEventMapper) {
    this.securityEventService = securityEventService;
    this.actionMapper = actionMapper;
    this.securityEventMapper = securityEventMapper;
  }

  @POST
  public Response postSecurityEvent(@Valid SecurityEventRequest request){
    SecurityEvent securityEvent = this.securityEventMapper.toSecurityEvent(request);
    List<Action> actions = this.securityEventService.processEvent(securityEvent);
    ActionsResponseDTO actionsResponseDto = this.actionMapper.toResponseDto(actions);
    return Response.status(200).entity(actionsResponseDto).build();
  }
}
