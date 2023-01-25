package servlets.auth;

import service.Service;

import javax.ejb.EJB;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    @EJB
    private Service service;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("text/html");
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");
        System.out.println(username);
        System.out.println(password);
        System.out.println(service);
        if (service.validateLogin(username, password)) {
            HttpSession session = request.getSession();
            session.setAttribute("username", username);
            RequestDispatcher rd = request.getRequestDispatcher("first");
            rd.forward(request, response);

        } else {
            out.print("Sorry, username or password error!");
            request.getRequestDispatcher("index.html").include(request, response);
        }
        out.close();
    }
}
