package ca.ulaval.glo4002.application.interfaces.rest.filters;

import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class RequestLoggerFilter implements ContainerResponseFilter{

  private final Logger logger = LoggerFactory.getLogger(RequestLoggerFilter.class);

  @Override
  public void filter(ContainerRequestContext requestContext,
      ContainerResponseContext responseContext){
    var statusCode = responseContext.getStatusInfo().getStatusCode();
    var statusName = responseContext.getStatusInfo().getReasonPhrase();
    var path = requestContext.getUriInfo().getPath();
    var method = requestContext.getMethod();
    if (statusCode < 400){
      this.logger.info(statusCode + " " + statusName + " - HTTP " + method + " /" + path);
    } else{
      this.logger.error(statusCode + " " + statusName + " - HTTP " + method + " /" + path);
    }
  }
}
