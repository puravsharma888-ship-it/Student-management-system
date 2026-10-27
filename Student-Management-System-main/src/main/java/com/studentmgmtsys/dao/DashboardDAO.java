package com.studentmgmtsys.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** Read-only queries that feed the teacher and manager dashboards. */
public class DashboardDAO {

    private int count(String sql) {
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (Exception e) {
            System.err.println("Dashboard count failed: " + e.getMessage());
            return 0;
        }
    }

    public int countStudents() { return count("SELECT COUNT(*) FROM students"); }
    public int countTeachers() { return count("SELECT COUNT(*) FROM teacherProfile"); }
    public int countActiveCourses() { return count("SELECT COUNT(*) FROM all_courses WHERE status = 'active'"); }
    public int countRegistrations() { return count("SELECT COUNT(*) FROM registered_course"); }
    public int countPendingComplaints() { return count("SELECT COUNT(*) FROM complain WHERE status = 'pending'"); }

    /** Teacher profile as label/value pairs (empty map when the teacher has no profile row). */
    public Map<String, String> getTeacherProfile(int userId) {
        Map<String, String> profile = new LinkedHashMap<>();
        String sql = "SELECT full_name, email, phone, department, experience, education, status "
                + "FROM teacherProfile WHERE user_id = ? LIMIT 1";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    profile.put("Full name", rs.getString("full_name"));
                    profile.put("Email", rs.getString("email"));
                    profile.put("Phone", rs.getString("phone"));
                    profile.put("Department", rs.getString("department"));
                    profile.put("Experience", rs.getString("experience"));
                    profile.put("Education", rs.getString("education"));
                    profile.put("Status", rs.getString("status"));
                }
            }
        } catch (Exception e) {
            System.err.println("Teacher profile query failed: " + e.getMessage());
        }
        return profile;
    }

    /** Each row: code, name, branch, semester, credits, status, enrolled students. */
    public List<String[]> getCourseEnrolments() {
        List<String[]> rows = new ArrayList<>();
        String sql = "SELECT c.course_code, c.course_name, c.branch, c.semester, c.credits, c.status, "
                + "COUNT(rc.id) AS enrolled "
                + "FROM all_courses c LEFT JOIN registered_course rc ON rc.course_id = c.course_id "
                + "GROUP BY c.course_id, c.course_code, c.course_name, c.branch, c.semester, c.credits, c.status "
                + "ORDER BY c.branch, c.semester, c.course_code";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                rows.add(new String[]{
                        rs.getString("course_code"), rs.getString("course_name"), rs.getString("branch"),
                        String.valueOf(rs.getInt("semester")), String.valueOf(rs.getInt("credits")),
                        rs.getString("status"), String.valueOf(rs.getInt("enrolled"))});
            }
        } catch (Exception e) {
            System.err.println("Course enrolment query failed: " + e.getMessage());
        }
        return rows;
    }
}
