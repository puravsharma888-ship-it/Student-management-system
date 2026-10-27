package com.studentmgmtsys.controller;

import com.studentmgmtsys.dao.StudentDAO;
import com.studentmgmtsys.dao.StudentProfileDAO;
import com.studentmgmtsys.dao.UserDAO;
import com.studentmgmtsys.model.Student;
import com.studentmgmtsys.model.StudentProfile;
import com.studentmgmtsys.model.User;
import com.studentmgmtsys.util.Validator;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

// If you want to wire DAOs later, uncomment these and the code inside TODO
// import com.studentmgmtsys.dao.DBConnection;
// import com.studentmgmtsys.dao.UserDAO;
// import com.studentmgmtsys.dao.StudentDAO;

@WebServlet({"/registerStudent", "/registerStudentForm"})
public class RegisterStudentController extends HttpServlet {
    
    // Handle GET requests - show the registration form
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // Check if user has valid session and admin role
        HttpSession session = request.getSession(false);
        if (session == null || !"admin".equals(session.getAttribute("role"))) {
            response.sendRedirect("index.jsp");
            return;
        }
        
        // Forward to register student JSP - session is automatically preserved
        RequestDispatcher rd = request.getRequestDispatcher("admin/registerStudent.jsp");
        rd.forward(request, response);
    }
    
    // Handle POST requests - process form submission (existing code)
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Only a logged-in administrator may register students
        HttpSession adminSession = request.getSession(false);
        if (adminSession == null || !"admin".equals(adminSession.getAttribute("role"))) {
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            return;
        }
        request.setCharacterEncoding("UTF-8");

        User user = new User();
        Student student = new Student();
        StudentProfile profile = new StudentProfile();

        // collect form data
        String username = trim(request.getParameter("stud_username"));
        String password = request.getParameter("stud_password");
        String status = trim(request.getParameter("status"));

        String registrationNo = trim(request.getParameter("registrationNo"));
        String department = trim(request.getParameter("department"));
        String semesterText = trim(request.getParameter("semester")).replace("Sem ", "");
        String yearText = trim(request.getParameter("admissionYear"));

        String fullName = trim(request.getParameter("fullName"));
        String email = trim(request.getParameter("email"));
        String phone = trim(request.getParameter("phone"));

        // ---- server-side validation ----
        int semester = Validator.toInt(semesterText);
        int admissionYear = Validator.toInt(yearText);
        int maxYear = java.time.Year.now().getValue() + 1;
        String error = null;
        if (!Validator.username(username)) {
            error = "Username must be 3-30 characters (letters, digits, . _ -).";
        } else if (!Validator.password(password)) {
            error = "Password must be at least 6 characters.";
        } else if (!"active".equals(status) && !"inactive".equals(status)) {
            error = "Select a valid account status.";
        } else if (!Validator.digits(registrationNo)) {
            error = "Registration number must contain digits only.";
        } else if (Validator.blank(department)) {
            error = "Select a department.";
        } else if (semester < 1 || semester > 8) {
            error = "Semester must be between 1 and 8.";
        } else if (admissionYear < 2000 || admissionYear > maxYear) {
            error = "Admission year must be between 2000 and " + maxYear + ".";
        } else if (!Validator.personName(fullName)) {
            error = "Enter a valid full name (letters and spaces only).";
        } else if (!Validator.email(email)) {
            error = "Enter a valid e-mail address.";
        } else if (!Validator.phone(phone)) {
            error = "Student mobile number must be exactly 10 digits.";
        } else if (Validator.blank(request.getParameter("address"))) {
            error = "Address is required.";
        } else if (!Validator.pastDate(trim(request.getParameter("dob")))) {
            error = "Enter a valid date of birth in the past.";
        } else if (!Validator.personName(request.getParameter("fatherName"))
                || !Validator.personName(request.getParameter("motherName"))) {
            error = "Enter valid father's and mother's names.";
        } else if (!Validator.phone(trim(request.getParameter("fatherMobile")))
                || !Validator.phone(trim(request.getParameter("motherMobile")))) {
            error = "Parent mobile numbers must be exactly 10 digits.";
        } else if (Validator.blank(request.getParameter("education"))) {
            error = "Education is required.";
        }
        if (error != null) {
            request.setAttribute("errorMessage", error);
            request.getRequestDispatcher("admin/registerStudent.jsp").forward(request, response);
            return;
        }

        // set user data
        user.setUsername(username);
        user.setPassword(password);
        user.setRole("student");
        user.setStatus(status);

        // DAO init
        UserDAO userDAO = new UserDAO();
        StudentDAO studentDAO = new StudentDAO();
        StudentProfileDAO profileDAO = new StudentProfileDAO();

        // Check if username already exists
        if (userDAO.isUsernameExists(username)) {
            request.setAttribute("errorMessage", "Username '" + username + "' already exists. Please choose a different username.");
            RequestDispatcher rd = request.getRequestDispatcher("admin/registerStudent.jsp");
            rd.forward(request, response);
            return;
        }

        // save user
        int userId = 0;
        try {
            userId = userDAO.registerUser(user);
        } catch (Exception e) {
            // Handle any database errors gracefully
            String errorMsg = "Registration failed: ";
            if (e.getMessage().contains("Duplicate entry")) {
                errorMsg += "Username already exists. Please choose a different username.";
            } else {
                errorMsg += "Database error occurred. Please try again.";
            }
            request.setAttribute("errorMessage", errorMsg);
            RequestDispatcher rd = request.getRequestDispatcher("admin/registerStudent.jsp");
            rd.forward(request, response);
            return;
        }

        if (userId > 0) {
            // save student
            student.setUserId(userId);
            student.setRegistrationNo(registrationNo);
            student.setDepartment(department);
            student.setSemester(semester);
            student.setAdmissionYear(admissionYear);
            student.setStatus(status);
            try {
                studentDAO.addStudent(student);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            // save profile
            profile.setFullName(fullName);
            profile.setEmail(email);
            profile.setPhone(phone);
            profile.setAddress(request.getParameter("address"));
//            try {
//                profile.setDob(java.sql.Date.valueOf(request.getParameter("dob")));
//            } catch (Exception e) {
//                profile.setDob(null);
//            }

            profile.setDob(request.getParameter("dob"));

            profile.setEducation(request.getParameter("education"));
            profile.setFatherName(request.getParameter("fatherName"));
            profile.setFatherMobile(request.getParameter("fatherMobile"));
            profile.setMotherName(request.getParameter("motherName"));
            profile.setMotherMobile(request.getParameter("motherMobile"));

            try {
                profileDAO.addProfile(profile, userId);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            request.getSession().setAttribute("status", "success");
            response.sendRedirect(request.getContextPath() + "/DashboardServlet");
        } else {
            request.setAttribute("errorMessage", "Failed to create user account. Please try again.");
            RequestDispatcher rd = request.getRequestDispatcher("admin/registerStudent.jsp");
            rd.forward(request, response);
        }
    }

    private static String trim(String s) {
        return s == null ? "" : s.trim();
    }
}
