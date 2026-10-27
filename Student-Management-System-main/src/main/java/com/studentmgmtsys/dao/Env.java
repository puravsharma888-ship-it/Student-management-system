package com.studentmgmtsys.dao;

/** Local MySQL settings. Update these if your MySQL account differs. */
public final class Env {
    private Env() { }
    public static final String DB_URL =
            "jdbc:mysql://localhost:3306/studentManagementSystem?useSSL=false&serverTimezone=UTC";
    public static final String DB_USER = "root";
    public static final String DB_PASSWORD = "12345678";
}
