import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.util.resource.Resource;
import servlets.*;

import javax.servlet.MultipartConfigElement;
import java.io.File;
import java.net.URI;
import java.nio.file.Path;

public class JettyServer {

    public static void main(String[] args) throws Exception {

        Server server = new Server(8080);
        ServletContextHandler servletContextHandler = new ServletContextHandler();
        String docBase = "src/main/webapp/";
        servletContextHandler.setResourceBase(docBase);
//        Path webrootPath = new File(docBase).toPath().toRealPath();
//        URI webrootUri = webrootPath.toUri();
//        Resource webroot = Resource.newResource(webrootUri);
//
        servletContextHandler.setContextPath("/");
//        servletContextHandler.setBaseResource(webroot);
        server.setHandler(servletContextHandler);
        servletContextHandler.addServlet(AllRecipesServlet.class, "");
        servletContextHandler.addServlet(AllRecipesServlet.class, "/recipes");
        servletContextHandler.addServlet(RecipeDetailsServlet.class, "/details");
        servletContextHandler.addServlet(ImageServlet.class, "/image/*");
        servletContextHandler.addServlet(AddRecipeServlet.class, "/add").getRegistration().setMultipartConfig(new MultipartConfigElement("/pictures"));
        servletContextHandler.addServlet(NotFoundServlet.class, "/error");
        System.out.println("Start server jetty embedded");
        server.start();
        server.join();
    }
}
