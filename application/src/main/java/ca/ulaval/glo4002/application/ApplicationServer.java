package ca.ulaval.glo4002.application;

import ca.ulaval.glo4002.application.infrastructure.config.ApplicationConfig;
import ca.ulaval.glo4002.application.infrastructure.config.DependencyInjector;
import org.eclipse.jetty.ee10.servlet.ServletContextHandler;
import org.eclipse.jetty.ee10.servlet.ServletHolder;
import org.eclipse.jetty.server.Server;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationServer implements Runnable{
  private final Logger logger = LoggerFactory.getLogger(ApplicationServer.class);
  private final ApplicationConfig config;
  private final DependencyInjector dependencyInjector;

  public ApplicationServer() {
    this.config = new ApplicationConfig();
    this.dependencyInjector = new DependencyInjector(config);
  }

  public static void main(String[] args){
    new ApplicationServer().run();
  }

  public void run(){
    Server server = createServer();
    configureServerContext(server);

    try{
      server.start();
      this.logger.info("Server started on port {}",this.config.getServerPort());
      server.join();
    } catch (Exception e){
      this.logger.error("Error starting server",e);
    } finally{
      shutdownServer(server);
    }
  }

  private Server createServer(){
    return new Server(config.getServerPort());
  }

  private void configureServerContext(Server server){
    ServletContextHandler contextHandler = new ServletContextHandler("/");
    server.setHandler(contextHandler);

    ResourceConfig resourceConfig = dependencyInjector.createResourceConfig();
    ServletContainer container = new ServletContainer(resourceConfig);
    ServletHolder servletHolder = new ServletHolder(container);
    contextHandler.addServlet(servletHolder,"/*");
  }

  private void shutdownServer(Server server){
    if (server.isRunning()){
      server.destroy();
    }
  }
}
