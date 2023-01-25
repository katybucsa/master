package servlets;


import model.Book;
import model.Loan;
import model.Review;
import repository.userRepository.Role;
import service.Service;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Objects;

@WebServlet("/bookDetails")
public class BookDetailsServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            int bookId = Integer.parseInt(request.getParameter("id"));
            Role role = (Role) session.getAttribute("role");
            Book book = service.findBookById(bookId);
            Loan loan = service.findCurrentLoanForBook(bookId);
            out.println("<html>");
            out.println("<style>\n" +
                    "table, th, td {\n" +
                    "  border: 1px solid black;\n" +
                    "}\n" +
                    ".unchecked{\n" +
                    "    color: black;\n" +
                    "}\n" +
                    ".checked {\n" +
                    "    color: gold;\n" +
                    "}\n" +
                    "div.rev {border-style: solid;}" +
                    "</style>");
            out.println("<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">");
            out.println("<body>");
            out.println("<a href=\"first\">" + "Lista carti" + "</a>&emsp;");
            if (role.equals(Role.LIBRARIAN)) {
                out.println("<a href=\"getBook\">" + "Cauta carte" + "</a>&emsp;");
                out.println("<a href=\"addBook\">" + "Adauga carte" + "</a>&emsp;");
            } else {
                out.println("<a href=\"myLoans\">" + "Imprumuturile mele" + "</a>&emsp;");
            }
            out.println("<a href=\"logout\">" + "Logout" + "</a><br/><br/>");
            out.println("<table style=\"float:center\">");
            out.println("<h3><p>Detalii carte</p></h3>");
            if (role.equals(Role.LIBRARIAN)) {
                if (Objects.isNull(loan))
                    out.println("<a href=\"loan?bookId=" + bookId + "\">" + "Imprumuta" + "</a>&emsp;");
                else
                    out.println("<a href=\"back?bookId=" + bookId + "\">" + "Returneaza" + "</a>&emsp;");
                out.println("<form method=\"POST\" action=\"deleteBook\">");
                out.println("<input type=\"hidden\" name=\"bookId\" value=\"" + bookId + "\">");
                out.println("<p><input type=\"submit\" value=\"Sterge carte\"/></p>");
                out.println("</form>");
            }
            out.println("<div>" +
                    "<p>Titlu:  " + book.getTitle() + "</p>" +
                    "<p>Autor:  " + book.getAuthor() + "</p>" +
                    "<p>Anul publicarii:  " + book.getPublishedYear() + "</p>" +
                    "<p>ISBN:  " + book.getIsbn() + "</p>");
            if (!Objects.isNull(loan))
                out.println("<p>Imprumutata de:  " + loan.getPerson().getFullName() + "</p>");
            out.println("</div><br/>");

            out.println("<h3><p>Review-uri carte</p></h3>");
            List<Review> reviews = service.findAllReviewsByBookId(bookId);
            reviews.forEach(review -> {
                out.println("<div class=\"rev\">" +
                        "<p>Reviewer:  " + review.getPerson().getFullName() + "</p>" +
                        "<p>Data:  " + review.getPublishedDate() + "</p>" +
                        "<p>Continut:  " + review.getContent() + "</p>");
                out.println("<p>Rating:  </p>");
                int i;
                for (i = 0; i < review.getRating(); i++) {
                    out.println("<span class=\"fa fa-star checked\"></span>");
                }
                for (; i < 5; i++)
                    out.println("<span class=\"fa fa-star unchecked\"></span>");
                out.println("</div><br/>");
            });
            out.println("<form method=\"POST\" action=\"addReview\">");
            out.println("<p>Review: <textarea name=\"content\" rows=\"5\" cols=\"50\" placeholder=\"Adauga review\"></textarea></p>");
            out.println("<span>Rating:<select name=\"rating\">");
            out.println("<option value=\"1\">1</option>");
            out.println("<option value=\"2\">2</option>");
            out.println("<option value=\"3\">3</option>");
            out.println("<option value=\"4\">4</option>");
            out.println("<option value=\"5\">5</option>");
            out.println("</select></span><!--<span class=\"rating\"><span>☆</span><span>☆</span><span>☆</span><span>☆</span><span>☆</span></span></p></span>-->");
            out.println("<input type=\"hidden\" name=\"id\" value=\"" + bookId + "\">");
            out.println("<p><input type=\"submit\" value=\"Adauga review\"/></p>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        } else {
            out.print("Please login first");
            request.getRequestDispatcher("index.html").include(request, response);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        doGet(request, response);
    }
}
