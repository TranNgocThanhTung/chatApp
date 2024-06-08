package com.example.btck2.Model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class connectDB {
    // JDBC URL, username and password of MySQL server
    private static final String URL = "jdbc:mysql://localhost:3306/filetrans_db";
    private static final String USER = "root";
    private static final String PASSWORD = "tungnebay123";

    // JDBC variables for opening and managing connection
    private static Connection connection;

    public static Connection connect() {
        if (connection == null) {
            try {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                // Establish the connection
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("Connection to MySQL database established successfully.");
            } catch (ClassNotFoundException e) {
                System.err.println("MySQL JDBC Driver not found. Include the library in your project.");
                e.printStackTrace();
            } catch (SQLException e) {
                System.err.println("Connection to MySQL database failed.");
                e.printStackTrace();
            }
        }
        return connection;
    }

    public static void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Connection to MySQL database closed successfully.");
            } catch (SQLException e) {
                System.err.println("Failed to close connection to MySQL database.");
                e.printStackTrace();
            }
        }
    }
}
