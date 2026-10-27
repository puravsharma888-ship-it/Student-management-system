<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.*,com.studentmgmtsys.model.Complain" %>
<%!
    static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    HttpSession s = request.getSession(false);
    if (s == null || !"manager".equalsIgnoreCase(String.valueOf(s.getAttribute("role")))) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    if (request.getAttribute("courseRows") == null) {   // opened directly, not through the controller
        response.sendRedirect(request.getContextPath() + "/DashboardServlet");
        return;
    }
    @SuppressWarnings("unchecked")
    List<String[]> rows = (List<String[]>) request.getAttribute("courseRows");
    @SuppressWarnings("unchecked")
    List<Complain> complaints = (List<Complain>) request.getAttribute("complaints");
    if (complaints == null) complaints = new ArrayList<>();
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Manager Dashboard</title>
    <link rel="stylesheet" href="assets/css/styles.css">
    <style>
        .wrap { max-width: 1000px; margin: 20px auto; padding: 0 18px; }
        .stats { display: flex; gap: 14px; flex-wrap: wrap; margin-bottom: 20px; }
        .stat { flex: 1; min-width: 150px; background: #fff; border-radius: 10px; padding: 16px 18px; box-shadow: 0 2px 10px rgba(0,0,0,.07); }
        .stat b { display: block; font-size: 28px; color: #1f4e9c; }
        .stat span { color: #666; font-size: 13px; }
        .panel { background: #fff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,.07); padding: 20px 24px; margin-bottom: 20px; }
        .panel h2 { margin: 0 0 14px; font-size: 18px; }
        table.t { width: 100%; border-collapse: collapse; font-size: 14px; }
        table.t th { background: #1f4e9c; color: #fff; text-align: left; padding: 9px 10px; }
        table.t td { padding: 9px 10px; border-bottom: 1px solid #eee; vertical-align: top; }
        .badge { padding: 2px 8px; border-radius: 10px; font-size: 12px; }
        .active, .complete { background: #e1f6e6; color: #1b7a34; }
        .inactive { background: #fde8e8; color: #b42318; } .pending { background: #fff3d6; color: #9a6700; }
        .muted { color: #777; }
    </style>
</head>
<body>
<div class="header">
    <div class="header-left">
        <a href="DashboardServlet" class="home-btn"><img id="homeIcon" src="assets/icons/home.png" width="20px"></a>
        <img src="assets/icons/univ.png" height="45px" alt="Demo University">
    </div>
    <h1 id="headerTitle">Welcome, Manager</h1>
    <div class="header-right">
        <form action="LogoutServlet" method="post" class="logout-form">
            <button type="submit" class="logout-btn">Log Out</button>
        </form>
    </div>
</div>

<div class="wrap">
    <div class="stats">
        <div class="stat"><b><%= request.getAttribute("studentCount") %></b><span>Students</span></div>
        <div class="stat"><b><%= request.getAttribute("teacherCount") %></b><span>Teachers</span></div>
        <div class="stat"><b><%= request.getAttribute("courseCount") %></b><span>Active courses</span></div>
        <div class="stat"><b><%= request.getAttribute("registrationCount") %></b><span>Course registrations</span></div>
        <div class="stat"><b><%= request.getAttribute("pendingComplaints") %></b><span>Pending complaints</span></div>
    </div>

    <div class="panel">
        <h2>Courses and Enrolments</h2>
        <% if (rows.isEmpty()) { %><p class="muted">No courses have been created yet.</p><% } else { %>
        <table class="t">
            <tr><th>Code</th><th>Course</th><th>Branch</th><th>Sem</th><th>Credits</th><th>Status</th><th>Enrolled</th></tr>
            <% for (String[] r : rows) { %>
            <tr>
                <td><%= esc(r[0]) %></td><td><%= esc(r[1]) %></td><td><%= esc(r[2]) %></td>
                <td><%= esc(r[3]) %></td><td><%= esc(r[4]) %></td>
                <td><span class="badge <%= "active".equals(r[5]) ? "active" : "inactive" %>"><%= esc(r[5]) %></span></td>
                <td><%= esc(r[6]) %></td>
            </tr>
            <% } %>
        </table>
        <% } %>
    </div>

    <div class="panel">
        <h2>Complaints (read-only overview)</h2>
        <% if (complaints.isEmpty()) { %><p class="muted">No complaints have been submitted.</p><% } else { %>
        <table class="t">
            <tr><th>#</th><th>Title</th><th>Description</th><th>Date</th><th>Status</th></tr>
            <% for (Complain c : complaints) { %>
            <tr>
                <td><%= c.getId() %></td><td><%= esc(c.getTitle()) %></td><td><%= esc(c.getDescription()) %></td>
                <td><%= esc(c.getCreatedDate()) %></td>
                <td><span class="badge <%= "complete".equals(c.getStatus()) ? "complete" : "pending" %>"><%= esc(c.getStatus()) %></span></td>
            </tr>
            <% } %>
        </table>
        <% } %>
        <p class="muted" style="margin-bottom:0">Complaints are resolved by the administrator in the Grievance module.</p>
    </div>
</div>
</body>
</html>
