package servlets;

import model.Recipe;
import repository.RecipeRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;

@WebServlet("/details")
public class RecipeDetailsServlet extends HttpServlet {

    private final RecipeRepository recipeRepository = RecipeRepository.getInstance();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        Recipe recipe;

        try {
            recipe = recipeRepository.getById(Integer.parseInt(req.getParameter("id")));
            if (Objects.isNull(recipe)) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recipe not found!");
                return;
            }
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Recipe not found!");
            return;
        }

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<html>");
        out.println("<head>");
        out.println("<style>");
        out.println("body {\n" +
                "    background: #fafad2;\n" +
                "    font-family: courier;\n" +
                "    color: tomato;\n" +
                "}\n" +
                "\n" +
                "img {\n" +
                "    width: 400px;\n" +
                "    border: 4px solid tomato;\n" +
                "}\n" +
                "\n" +
                "ul {\n" +
                "    list-style-type: square;\n" +
                "}\n" +
                "\n" +
                "hr {\n" +
                "    height: 2px;\n" +
                "    border: none;\n" +
                "    background-color: tomato;\n" +
                "}\n" +
                "\n" +
                "\n" +
                ".rating {\n" +
                "    unicode-bidi: bidi-override;\n" +
                "    direction: rtl;\n" +
                "    text-align: center;\n" +
                "}\n" +
                "\n" +
                ".rating > span {\n" +
                "    display: inline-block;\n" +
                "    position: relative;\n" +
                "    width: 1.1em;\n" +
                "}\n" +
                "\n" +
                ".inl{\n" +
                "    display: inline-block;\n" +
                "}\n" +
                "\n" +
                ".rating > span:hover,\n" +
                ".rating > span:hover ~ span {\n" +
                "    color: transparent;\n" +
                "}\n" +
                "\n" +
                ".rating > span:hover:before,\n" +
                ".rating > span:hover ~ span:before {\n" +
                "    content: \"\\2605\";\n" +
                "    position: absolute;\n" +
                "    left: 0;\n" +
                "    color: gold;\n" +
                "}\n" +
                "\n" +
                ".unchecked{\n" +
                "    color: black;\n" +
                "}\n" +
                "\n" +
                ".checked {\n" +
                "    color: gold;\n" +
                "}\n");
        out.println("</style>");
        out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
        out.println("<title>Recipe book</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>" + recipe.getName() + " </h1>");
        out.println("<p><h3>Dificultate: ");
        int i;
        for (i = 0; i < recipe.getDificulty(); i++) {
            out.println("<span class=\"fa fa-star checked\"></span>");
        }
        for (; i < 5; i++)
            out.println("<span class=\"fa fa-star unchecked\"></span>");
        out.println("</h3></p>");
        out.println("<p><h3>Timp de preparare: " + recipe.getPreparingTime() + " de minute</h3></p>");
        out.println("<img src=\"" + req.getContextPath() + "/image/" + recipe.getId() + ".jpg\">");
        out.println("<h3>Ingredients:</h3>");
        out.println("<ul>");
        recipe.getIngredients().forEach(in -> {
            out.println("<li>" + in + "</li>");
        });
        out.println("</ul>");
        out.println("<h3>Mod de preparare</h3>");
        out.println("<p>" + recipe.getMethod() + "</p>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
}
