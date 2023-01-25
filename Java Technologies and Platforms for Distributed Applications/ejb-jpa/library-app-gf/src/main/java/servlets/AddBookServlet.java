package servlets;


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

@WebServlet("/addBook")
public class AddBookServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Role role = (Role) session.getAttribute("role");
            if (role.equals(Role.LIBRARIAN)) {
                String title = request.getParameter("title");
                String author = request.getParameter("author");
                String isbn = request.getParameter("isbn");
                int publishedYear = Integer.parseInt(request.getParameter("publishedYear"));
                service.addBook(title, author, isbn, publishedYear);
                response.sendRedirect(request.getContextPath() + "/first");
            } else {
                out.print("Please login first");
                request.getRequestDispatcher("index.html").include(request, response);
            }
        } else {
            out.print("Please login first");
            request.getRequestDispatcher("index.html").include(request, response);
        }
        out.close();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Role role = (Role) session.getAttribute("role");
            if (role.equals(Role.LIBRARIAN)) {
                out.println("<html>");
                out.println("<body>");
                out.println("<a href=\"first\">" + "Lista carti" + "</a>&emsp;");
                out.println("<a href=\"getBook\">" + "Cauta carte" + "</a>&emsp;");
                out.println("<a href=\"addBook\">" + "Adauga carte" + "</a>&emsp;");
                out.println("<a href=\"logout\">" + "Logout" + "</a><br/><br/>");
                out.println("<form method=\"POST\" action=\"addBook\">");
                out.println("<p>Titlu:<input type=\"text\" name=\"title\" size=\"50\"></p><br/>");
                out.println("<p>Autor:<input type=\"text\" name=\"author\" size=\"50\"></p><br/>");
                out.println("<p>Anul publicarii:<input type=\"number\" min=\"1000\" max=\"2021\" name=\"publishedYear\" size=\"50\"></p><br/>");
                out.println("<p>ISBN:<input type=\"text\" name=\"isbn\" size=\"50\"></p><br/>");
                out.println("<p><input type=\"submit\" value=\"Adauga carte\"/></p>");
                out.println("</form>");
                out.println("</body>");
                out.println("</html>");
            } else {
                out.print("Please login first");
                request.getRequestDispatcher("index.html").include(request, response);
            }
        } else {
            out.print("Please login first");
            request.getRequestDispatcher("index.html").include(request, response);
        }
        out.close();
    }
}
