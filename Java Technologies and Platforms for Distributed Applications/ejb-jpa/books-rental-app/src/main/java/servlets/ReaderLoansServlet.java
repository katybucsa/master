package servlets;


import model.Book;
import model.Loan;
import model.User;
import repository.userRepository.Role;
import service.Service;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
            List<Loan> loans=service.findAllLoansForReader(username);
            out.println("<html>");
            out.println("<body>");
            out.println("Hello, " + username + " and Welcome to library<br/>");
            out.println("<a href=\"first\">" + "Lista carti" + "</a>");
//            if (user.getRole().equals(Role.LIBRARIAN)) {
//                out.println("<a href=\"loan\">" + "Imprumuta" + "</a>");
//                out.println("<a href=\"back\">" + "Returnare" + "</a>");
//                out.println("<a href=\"addBook\">" + "Adauga carte" + "</a>");
//            } else {
//                out.println("<a href=\"myLoans\">" + "Imprumuturile mele" + "</a>");
//            }
            out.println("<a href=\"logout\">" + "Logout" + "</a><br/><br/>");
            List<Book> allBooks = service.findAllBooks();
            out.println("<ul style=\"list-style: none\">");
            allBooks.forEach(book -> {
                out.println("<li>");
                out.println("<a href=\"book?id=" + book.getId() + "\">" + book.getTitle() + ", " + book.getAuthor() + "</a>");
                out.println("</li>");
            });
            out.println("</ul>");
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
