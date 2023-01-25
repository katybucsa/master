package servlets;


import model.Book;
import model.Loan;
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

@WebServlet("/getBook")
public class GetBookServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Role role = (Role) session.getAttribute("role");
            out.println("<html>");
            out.println("<body>");
            out.println("<a href=\"first\">" + "Lista carti" + "</a>&emsp;");
            if (role.equals(Role.LIBRARIAN)) {
                out.println("<a href=\"getBook\">" + "Cauta carte" + "</a>&emsp;");
                out.println("<a href=\"addBook\">" + "Adauga carte" + "</a>&emsp;");
            } else {
                out.println("<a href=\"myLoans\">" + "Imprumuturile mele" + "</a>&emsp;");
            }
            out.println("<a href=\"logout\">" + "Logout" + "</a><br/><br/>");
            out.println("<form method=\"POST\" action=\"getBook\">");
            out.println("<p>Introduceti ISBN carte:<input type=\"text\" name=\"isbn\" size=\"50\"></p><br/>");
            out.println("<p><input type=\"submit\" value=\"Cauta carte\"/></p>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        } else {
            out.print("Please login first");
            request.getRequestDispatcher("index.html").include(request, response);
        }
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String isbn = request.getParameter("isbn");
        Book book = service.findBookByIsbn(isbn);
        response.sendRedirect(request.getContextPath() + "/bookDetails?id=" + book.getId());
    }
}
