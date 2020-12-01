import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;
import servlets.*;

import java.io.File;

public class TomcatServer {

    public static void main(String[] args) throws Exception {

        Tomcat server = new Tomcat();
        server.setPort(8080);
        String docBase = "src/main/webapp/";
        Context ctx = server.addWebapp("/", (new File(docBase)).getAbsolutePath());
        ctx.setAllowCasualMultipartParsing(true);
        Tomcat.addServlet(ctx, "recipes", new AllRecipesServlet());
        Tomcat.addServlet(ctx, "details", new RecipeDetailsServlet());
        Tomcat.addServlet(ctx, "error", new NotFoundServlet());
        Tomcat.addServlet(ctx, "add", new AddRecipeServlet());
        Tomcat.addServlet(ctx, "image", new ImageServlet());
        System.out.println("FDD");
        ctx.addServletMapping("", "recipes");
        ctx.addServletMapping("/add", "add");
        ctx.addServletMapping("/recipes", "recipes");
        ctx.addServletMapping("/details", "details");
        ctx.addServletMapping("/image/*", "image");
        ctx.addServletMapping("/error", "error");
        server.start();
        System.out.println("Start server Tomcat embedded");
        server.getServer().await();
    }
}
