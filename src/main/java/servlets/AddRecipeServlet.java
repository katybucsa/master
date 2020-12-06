package servlets;

import model.Recipe;
import repository.RecipeRepository;
import org.apache.commons.io.FilenameUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@MultipartConfig
@WebServlet("/add")
public class AddRecipeServlet extends HttpServlet {

    RecipeRepository recipeRepository = RecipeRepository.getInstance();
    private final String PATH = "C:\\Users\\Katy\\Documents\\M1S1\\TPJAD\\Laboratoare\\Servlet\\RecipeServletApp\\src\\main\\webapp";

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        String redir = req.getContextPath() + "/add";
        out.println("<!DOCTYPE html>");
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
        out.println("<title>Adaugare reteta</title>");
        out.println("</head>");
        out.println("<body>");
        out.println("<h3>Adauga reteta</h3>");
        out.println("<form method=\"POST\" action=\"" + redir + "\" enctype=\"multipart/form-data\">");
        out.println("<p>&emsp;&emsp;&emsp;&emsp;&emsp;&emsp;&nbsp;Denumire:&emsp;<input type=\"text\" name=\"name\" size=\"50\"></p>");
        out.println("<span class=\"inl\">&emsp;&emsp;&emsp;&emsp;&emsp;Dificultate:&emsp;<select name=\"dificulty\">");
        out.println("<option value=\"1\">1</option>");
        out.println("<option value=\"2\">2</option>");
        out.println("<option value=\"3\">3</option>");
        out.println("<option value=\"4\">4</option>");
        out.println("<option value=\"5\">5</option>");
        out.println("</select></span><!--<span class=\"rating\"><span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span></span></p></span>-->");
        out.println("<p>&ensp;Timp de preparare:&emsp;&nbsp;<input type=\"number\" name=\"time\" min=\"1\"/></p>");
        out.println("<p>&emsp;&emsp;&emsp;&emsp;&nbsp;Ingrediente:&emsp;<textarea name=\"ingredients\" placeholder=\"Introdu ingredientele\" rows=\"5\" cols=\"50\"></textarea></p>");
        out.println("<br/>");
        out.println("<p>Metoda de preparare:&emsp;<textarea name=\"method\" rows=\"5\" cols=\"50\" placeholder=\"Descrie metoda de preparare\"></textarea></p>");
        out.println("<br/>");
        out.println("<p>&emsp;&emsp;&emsp;&emsp;&emsp;Fotografie:&emsp;<input type=\"file\" name=\"image\" accept=\"image/*\"></p>");
        out.println("<br/>");
        out.println("<p><input type=\"submit\" value=\"Adauga\"/></p>");
        out.println("</form>");
        out.println("</body>");
        out.println("</html>");
        out.close();
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {

        try {
            String name = req.getParameter("name");
            int dificulty = Integer.parseInt(req.getParameter("dificulty"));
            int time = Integer.parseInt(req.getParameter("time"));
            List<String> ingredients = Arrays.asList(req.getParameter("ingredients").split("\n"));
            String method = req.getParameter("method");
            Recipe recipe = new Recipe(name, ingredients, method, dificulty, time);
            Integer id = recipeRepository.addRecipe(recipe);
            if (Objects.isNull(id)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Recipe not added!");
                return;
            }
            boolean uploaded = doUpload(req, id);
            if (!uploaded) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Recipe not added. Image not uploaded");
                recipeRepository.deleteRecipe(id);
                return;
            }
            resp.sendRedirect(req.getContextPath() + "/recipes");
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Recipe not added!");
        }
    }

    private boolean doUpload(HttpServletRequest request, int id) throws IOException, ServletException {

        Part filePart = request.getPart("image");
        String filename = getFileName(filePart);
        if (filename.isEmpty())
            return false;
        String fileUploadLocation = request.getRealPath("/") + "/pictures/";
        Path newUploadLocation = Paths.get(fileUploadLocation);
        String extension = FilenameUtils.getExtension(filename);
        String newFilename = id + "." + extension;
        Files.createDirectories(newUploadLocation);
        try (InputStream inputStream = filePart.getInputStream()) {

            Files.copy(inputStream, newUploadLocation.resolve(newFilename), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private String getFileName(Part part) {

        String contentDisp = part.getHeader("content-disposition");
        String[] tokens = contentDisp.split(";");
        for (String token : tokens) {
            if (token.trim().startsWith("filename")) {
                return token.substring(token.indexOf("=") + 2, token.length() - 1);
            }
        }
        return "";
    }
}
