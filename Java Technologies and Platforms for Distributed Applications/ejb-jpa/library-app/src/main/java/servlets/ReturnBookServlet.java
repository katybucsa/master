package servlets;


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

@WebServlet("/back")
public class ReturnBookServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
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
            service.updateLoan(bookId);
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
            Loan loan = service.findCurrentLoanForBook(bookId);
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

            out.println("<form method=\"POST\" action=\"back\">");
            out.println("<p>Numele cititorului este:" + loan.getPerson().getFullName() + "</p><br/>");
            out.println("<p>Id-ul de legitimatie al cititorului este:" + loan.getPerson().getBadgeId() + "</p><br/>");
            out.println("<p>Confirmati returnarea cartii?</p>");
            out.println("<input type=\"hidden\" name=\"id\" value=\"" + bookId + "\">");
            out.println("<p><input type=\"submit\" value=\"Returneaza carte\"/></p>");
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
