<%@ page contentType="text/html;charset=UTF-8" language="java" import="java.util.*" %>
<%!
    static String esc(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }
%>
<%
    response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
    HttpSession s = request.getSession(false);
    if (s == null || !"teacher".equalsIgnoreCase(String.valueOf(s.getAttribute("role")))) {
        response.sendRedirect(request.getContextPath() + "/index.jsp");
        return;
    }
    if (request.getAttribute("courseRows") == null) {   // opened directly, not through the controller
        response.sendRedirect(request.getContextPath() + "/DashboardServlet");
        return;
    }
    @SuppressWarnings("unchecked")
    Map<String, String> profile = (Map<String, String>) request.getAttribute("teacherProfile");
    @SuppressWarnings("unchecked")
    List<String[]> rows = (List<String[]>) request.getAttribute("courseRows");
    String name = (profile != null && profile.get("Full name") != null) ? profile.get("Full name") : String.valueOf(s.getAttribute("username"));
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8"/>
    <title>Teacher Dashboard</title>
    <link rel="stylesheet" href="assets/css/styles.css">
    <style>
        .wrap { max-width: 980px; margin: 20px auto; padding: 0 18px; }
        .panel { background: #fff; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,.07); padding: 20px 24px; margin-bottom: 20px; }
        .panel h2 { margin: 0 0 14px; font-size: 18px; color: #1a1a1a; }
        table.t { width: 100%; border-collapse: collapse; font-size: 14px; }
        table.t th { background: #1f4e9c; color: #fff; text-align: left; padding: 9px 10px; }
        table.t td { padding: 9px 10px; border-bottom: 1px solid #eee; }
        .kv td:first-child { color: #666; width: 160px; font-weight: 600; }
        .badge { padding: 2px 8px; border-radius: 10px; font-size: 12px; }
        .active { background: #e1f6e6; color: #1b7a34; } .inactive { background: #fde8e8; color: #b42318; }
        .muted { color: #777; }
    </style>
</head>
<body>
<div class="header">
    <div class="header-left">
        <a href="DashboardServlet" class="home-btn"><img id="homeIcon" src="assets/icons/home.png" width="20px"></a>
        <img src="assets/icons/univ.png" height="45px" alt="Demo University">
    </div>
    <h1 id="headerTitle">Welcome, <%= esc(name) %></h1>
    <div class="header-right">
        <form action="LogoutServlet" method="post" class="logout-form">
            <button type="submit" class="logout-btn">Log Out</button>
        </form>
    </div>
</div>

<div class="wrap">
    <div class="panel">
        <h2>My Profile</h2>
        <% if (profile == null || profile.isEmpty()) { %>
            <p class="muted">No teacher profile has been set up for this account yet. Please contact the administrator.</p>
        <% } else { %>
            <table class="t kv">
                <% for (Map.Entry<String, String> e : profile.entrySet()) { %>
                <tr><td><%= esc(e.getKey()) %></td><td><%= esc(e.getValue()) %></td></tr>
                <% } %>
            </table>
        <% } %>
    </div>

    <div class="panel">
        <h2>Courses and Enrolments</h2>
        <% if (rows.isEmpty()) { %>
            <p class="muted">No courses have been created yet.</p>
        <% } else { %>
        <table class="t">
            <tr><th>Code</th><th>Course</th><th>Branch</th><th>Sem</th><th>Credits</th><th>Status</th><th>Students enrolled</th></tr>
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
</div>
</body>
</html>
