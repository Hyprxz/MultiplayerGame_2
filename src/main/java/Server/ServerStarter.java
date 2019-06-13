package Server;

import Controllers.GameHandler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;

public class ServerStarter {
    public static void main (String args[]) {
        ResourceConfig resourceConfig = new ResourceConfig();
        resourceConfig.packages("Controllers");
        resourceConfig.register(MultiPartFeature.class);

        ServletHolder servletHolder = new ServletHolder(new ServletContainer(resourceConfig));
        Server server = new Server(8080);
        ServletContextHandler servletContextHandler = new ServletContextHandler(server, "/");
        servletContextHandler.addServlet(servletHolder, "/*");

        try {
            server.start();
            System.out.println("Started server");
            GameHandler.gameLoop();
            server.join();

        } catch (Exception e) {
            System.out.println("Server failed to start");
        }
    }
}
