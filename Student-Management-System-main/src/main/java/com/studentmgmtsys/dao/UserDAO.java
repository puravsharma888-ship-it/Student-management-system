package com.studentmgmtsys.dao;

import com.studentmgmtsys.model.User;
import com.studentmgmtsys.util.PasswordUtil;
import java.sql.*;

public class UserDAO {

    public User validateUser(String username, String password) {
        User user = null;
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("SELECT role, user_id, password FROM login WHERE username=? AND status='active'");
            ps.setString(1, username);
            rs = ps.executeQuery();

            if (rs.next()) {
                String stored = rs.getString("password");
                boolean ok;
                boolean legacyPlain = !PasswordUtil.isHashed(stored);
                if (legacyPlain) {
                    // Old plain-text row: accept once, then upgrade it to a hash below.
                    ok = stored != null && stored.equals(password);
                } else {
                    ok = PasswordUtil.verify(password, stored);
                }
                if (ok) {
                    user = new User();
                    user.setUserId(rs.getInt("user_id"));
                    user.setRole(rs.getString("role"));
                    if (legacyPlain) {
                        upgradePassword(con, user.getUserId(), password);
                    }
                }
            }

        } catch (Exception e) {
            System.err.println("Login validation failed. Check MySQL connection, database name, login table, and credentials.");
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return user;
    }

    private void upgradePassword(Connection con, int userId, String plain) {
        try (PreparedStatement up = con.prepareStatement("UPDATE login SET password=? WHERE user_id=?")) {
            up.setString(1, PasswordUtil.hash(plain));
            up.setInt(2, userId);
            up.executeUpdate();
        } catch (Exception e) {
            System.err.println("Could not upgrade password hash: " + e.getMessage());
        }
    }

    public boolean isUsernameExists(String username) {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("SELECT username FROM login WHERE username = ?");
            ps.setString(1, username);
            rs = ps.executeQuery();

            return rs.next();

        } catch (Exception e) {
            System.out.println("Error checking username: " + e.getMessage());
            return false;
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (con != null) con.close();
            } catch (Exception e) {
                System.out.println("Error closing resources: " + e.getMessage());
            }
        }
    }

    public int registerUser(User user) throws Exception {
        Connection con = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        int userId = -1;

        try {
            con = DBConnection.getConnection();
            ps = con.prepareStatement("INSERT INTO login(username, password, role, status) VALUES (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getUsername());
            ps.setString(2, PasswordUtil.hash(user.getPassword()));
            ps.setString(3, user.getRole());
            ps.setString(4, user.getStatus());
            ps.executeUpdate();

            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                userId = rs.getInt(1);
            }

        } finally {
            if (rs != null) rs.close();
            if (ps != null) ps.close();
            if (con != null) con.close();
        }

        return userId;
    }
}