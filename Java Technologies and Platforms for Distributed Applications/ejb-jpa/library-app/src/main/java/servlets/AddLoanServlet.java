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

@WebServlet("/loan")
public class AddLoanServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            Role role = (Role) session.getAttribute("role");
            out.println("<a href=\"first\">" + "Lista carti" + "</a>&emsp;");
            if (!role.equals(Role.LIBRARIAN)) {
                out.println("<a href=\"myLoans\">" + "Imprumuturile mele" + "</a>&emsp;");
            } else {
                out.println("<a href=\"getBook\">" + "Cauta carte" + "</a>&emsp;");
                out.println("<a href=\"addBook\">" + "Adauga carte" + "</a>&emsp;");
            }
            out.println("<a href=\"logout\">" + "Logout" + "</a><br/><br/>");
            int bookId = Integer.parseInt(request.getParameter("id"));
            String badgeId = request.getParameter("badgeId");
            service.addLoan(bookId, badgeId);
            response.sendRedirect(request.getContextPath() + "/bookDetails?id=" + bookId);
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
            int bookId = Integer.parseInt(request.getParameter("bookId"));
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
            out.println("<form method=\"POST\" action=\"loan\">");
            out.println("<p>Introduceti id-ul de legitimatie al cititorului:<input type=\"text\" name=\"badgeId\" size=\"50\"></p><br/>");
            out.println("<input type=\"hidden\" name=\"id\" value=\"" + bookId + "\">");
            out.println("<p><input type=\"submit\" value=\"Imprumuta carte\"/></p>");
            out.println("</form>");
            out.println("</body>");
            out.println("</html>");
        } else {
            out.print("Please login first");
            request.getRequestDispatcher("index.html").include(request, response);
        }
        out.close();
    }
}
