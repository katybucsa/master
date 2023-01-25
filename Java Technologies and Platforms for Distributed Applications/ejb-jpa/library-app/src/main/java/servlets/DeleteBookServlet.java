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

@WebServlet("/deleteBook")
public class DeleteBookServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        HttpSession session = request.getSession(false);
        if (session != null) {
            Role role = (Role) session.getAttribute("role");
            if (role.equals(Role.LIBRARIAN)) {
                int bookId = Integer.parseInt(request.getParameter("bookId"));
                service.deleteBook(bookId);
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
}
