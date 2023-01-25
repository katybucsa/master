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
import java.util.List;
import java.util.Objects;

@WebServlet("/myLoans")
public class ReaderLoansServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            Role role = (Role) session.getAttribute("role");
            List<Loan> loans = service.findAllLoansForReader(username);
            out.println("<html>");
            out.println("<style>\n" +
                    "table, th, td {\n" +
                    "  border: 1px solid black;\n" +
                    "}\n" +
                    "</style>");
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
            out.println("<p>Cartile imprumutate</p><br/><br/>");
            out.println("<tr><th>Titlul cartii</th><th>Autorul</th><th>Data imprumutului</th><th>Data returnarii</th></tr>");
            loans.forEach(loan -> {
                out.println("<tr>");
                out.println("<td>" + loan.getBook().getTitle() + "</td>");
                out.println("<td>" + loan.getBook().getAuthor() + "</td>");
                out.println("<td>" + loan.getLoanDate() + "</td>");
                if (Objects.isNull(loan.getReturnDate()))
                    out.println("<td>" + "Nereturnata" + "</td>");
                else
                    out.println("<td>" + loan.getReturnDate() + "</td>");
                out.println("</tr>");
            });
            out.println("</table>");
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
