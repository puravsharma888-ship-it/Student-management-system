package com.studentmgmtsys.controller;

import com.studentmgmtsys.dao.ComplainDAO;
import com.studentmgmtsys.dao.DashboardDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/** Manager home: read-only overview of students, courses, registrations and complaints. */
@WebServlet("/managerHome")
public class ManagerController extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"manager".equals(session.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        DashboardDAO dao = new DashboardDAO();
        request.setAttribute("studentCount", dao.countStudents());
        request.setAttribute("teacherCount", dao.countTeachers());
        request.setAttribute("courseCount", dao.countActiveCourses());
        request.setAttribute("registrationCount", dao.countRegistrations());
        request.setAttribute("pendingComplaints", dao.countPendingComplaints());
        request.setAttribute("courseRows", dao.getCourseEnrolments());
        request.setAttribute("complaints", new ComplainDAO().getAllComplaints());
        request.getRequestDispatcher("/managerDashboard.jsp").forward(request, response);
    }
}
