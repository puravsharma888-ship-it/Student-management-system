package com.studentmgmtsys.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/DashboardServlet")
public class DashboardController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute("role") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        String role = String.valueOf(session.getAttribute("role")).trim().toLowerCase();
        String page;
        switch (role) {
            case "admin": page = "/adminDashboard.jsp"; break;
            case "manager": page = "/managerHome"; break;
            case "teacher": page = "/teacherHome"; break;
            case "student": page = "/studentDashboard.jsp"; break;
            default:
                session.invalidate();
                response.sendRedirect(request.getContextPath() + "/index.jsp?error=invalidrole");
                return;
        }
        request.getRequestDispatcher(page).forward(request, response);
    }
}
