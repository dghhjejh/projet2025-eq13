package ca.ulaval.glo4002.application.interfaces.rest.resources;

import ca.ulaval.glo4002.application.app.AccessRequestService;
import ca.ulaval.glo4002.application.domain.access.AccessRequestEvent;
import ca.ulaval.glo4002.application.domain.access.AccessStatus;
import ca.ulaval.glo4002.application.interfaces.rest.mappers.AccessRequestMapper;
import ca.ulaval.glo4002.application.interfaces.rest.requests.AccessRequest;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Path("/demande-acces")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AccessRequestResource{

  private final AccessRequestService accessRequestService;
  private final AccessRequestMapper accessRequestMapper;

  @Inject
  public AccessRequestResource(AccessRequestService accessRequestService,
      AccessRequestMapper accessRequestMapper) {
    this.accessRequestService = accessRequestService;
    this.accessRequestMapper = accessRequestMapper;
  }

  @POST
  public Response postAccessRequest(@Valid AccessRequest request){
    AccessRequestEvent accessRequestEvent = this.accessRequestMapper.toAccessRequestEvent(request);
    AccessStatus accessStatus = this.accessRequestService.processAccessRequest(accessRequestEvent);

    Status httpStatus = mapToHttpStatus(accessStatus);
    return Response.status(httpStatus).build();
  }

  private Status mapToHttpStatus(AccessStatus accessStatus){
    return switch (accessStatus){
      case ALLOWED -> Status.OK;
      case DENIED -> Status.FORBIDDEN;
    };
  }
}
