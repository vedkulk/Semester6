package com.miniProject.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/AssignmentSubApp"; // Change to your DB name
    private static final String USER = "root";  // Your MySQL username
    private static final String PASSWORD = "dKZ9EBbEKgTXhCOM";  // Your MySQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
