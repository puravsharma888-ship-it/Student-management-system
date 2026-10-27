package com.studentmgmtsys.controller;

import com.studentmgmtsys.dao.DashboardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Teacher home: shows the teacher's profile and the course list with enrolment counts. */
@WebServlet("/teacherHome")
public class TeacherController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"teacher".equals(session.getAttribute("role"))
                || session.getAttribute("user_id") == null) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        int userId = (Integer) session.getAttribute("user_id");
        DashboardDAO dao = new DashboardDAO();
        request.setAttribute("teacherProfile", dao.getTeacherProfile(userId));
        request.setAttribute("courseRows", dao.getCourseEnrolments());
        request.getRequestDispatcher("/teacherDashboard.jsp").forward(request, response);
    }
}
