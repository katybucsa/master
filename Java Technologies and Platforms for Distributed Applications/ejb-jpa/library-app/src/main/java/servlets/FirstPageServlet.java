package servlets;


import model.Book;
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

@WebServlet("/first")
public class FirstPageServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            String username = (String) session.getAttribute("username");
            System.out.println("USERNAME: " + username);
            User user = service.findUserByUsername(username);
            session.setAttribute("role", user.getRole());
            out.println("<html>");
            out.println("<body>");
            out.println("Hello, " + username + " and Welcome to library<br/>");
            out.println("<a href=\"first\">" + "Lista carti" + "</a>&emsp;");
            if (user.getRole().equals(Role.LIBRARIAN)) {
                out.println("<a href=\"getBook\">" + "Cauta carte" + "</a>&emsp;");
                out.println("<a href=\"addBook\">" + "Adauga carte" + "</a>&emsp;");
            } else {
                out.println("<a href=\"myLoans\">" + "Imprumuturile mele" + "</a>&emsp;");
            }
            out.println("<a href=\"logout\">" + "Logout" + "</a><br/><br/>");
            List<Book> allBooks = service.findAllBooks();
            out.println("<ul style=\"list-style: none\">");
            allBooks.forEach(book -> {
                out.println("<li>");
                out.println("<a href=\"bookDetails?id=" + book.getId() + "\">" + book.getTitle() + ", " + book.getAuthor() + "</a>");
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
