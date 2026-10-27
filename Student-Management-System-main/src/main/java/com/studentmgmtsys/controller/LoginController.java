package com.studentmgmtsys.controller;

import com.studentmgmtsys.dao.UserDAO;
import com.studentmgmtsys.model.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/LoginServlet")
public class LoginController extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("error", "Enter both your login ID and password.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        User user = new UserDAO().validateUser(username.trim(), password);
        if (user == null) {
            request.setAttribute("error", "Login failed. Check the username/password and database connection.");
            request.getRequestDispatcher("/index.jsp").forward(request, response);
            return;
        }
        HttpSession session = request.getSession(true);
        session.setAttribute("username", username.trim());
        session.setAttribute("role", user.getRole());
        session.setAttribute("user_id", user.getUserId());
        response.sendRedirect(request.getContextPath() + "/DashboardServlet");
    }
}
