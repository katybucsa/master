package servlets;

import model.Recipe;
import model.RecipeRepository;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/recipes")
public class AllRecipesServlet extends HttpServlet {

    private final RecipeRepository recipeRepository = RecipeRepository.getInstance();

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        List<Recipe> recipes = recipeRepository.getAll();

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
        out.println("<title>Recipe book</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<p><h2>Recipes list</h2>       " +
                "<form action=\"add\"><input type=\"submit\" value=\"Adauga\"></form></p>");
        out.println("<div style=\"line-height: 2.5em\">");
        out.println("<ul style=\"list-style: none\">");

        recipes.forEach(recipe -> {
            out.println("<li>");
            out.println("<a href=\"details?id=" + recipe.getId() + "\">" + recipe.getName() + "</a>");
            out.println("</li>");
        });
        out.println("</ul>");
        out.println("</div>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }
}
