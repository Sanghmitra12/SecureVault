package com.securevault.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Singleton class to manage database connections
 */
public class DBConnection {
    private static DBConnection instance;
    private static final String DB_URL = "jdbc:mysql://localhost:3306/securevault";
    private final String DB_USER = "root";    // apna MySQL username
private final String DB_PASS = "Sh@ilja2602"; // apna MySQL password
    private Connection connection;
    
    // Private constructor to prevent instantiation
    private DBConnection() {
    //     try {
    //         // Load the SQLite JDBC driver
    //       //  Class.forName("org.sqlite.JDBC");
    //     } catch (ClassNotFoundException e) {
    //         System.err.println("SQLite JDBC Driver not found: " + e.getMessage());
    //     }
    }
    
    /**
     * Get the singleton instance of DBConnection
     * @return The DBConnection instance
     */
    public static synchronized DBConnection getInstance() {
        if (instance == null) {
            instance = new DBConnection();
        }
        return instance;
    }
    
    /**
     * Get a connection to the database
     * @return A Connection object
     * @throws SQLException If a database error occurs
     */
    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
          //  connection = DriverManager.getConnection(DB_URL);
            connection =  DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            // Disable auto-commit
         //   connection.setAutoCommit(false);
        }
        return connection;
    }
    
    /**
     * Close the database connection
     */
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
    
    /**
     * Commit the current transaction
     * @throws SQLException If a database error occurs
     */
    public void commit() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.commit();
        }
    }
    
    /**
     * Rollback the current transaction
     */
    public void rollback() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.rollback();
            }
        } catch (SQLException e) {
            System.err.println("Error rolling back transaction: " + e.getMessage());
        }
    }
    /**
 * Initialize database tables if they don't exist
 */
public void initDatabase() {
    String createUsers = "CREATE TABLE IF NOT EXISTS users (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(255) UNIQUE NOT NULL, " +
            "password_hash VARCHAR(255) NOT NULL, " +
            "is_admin TINYINT(1) DEFAULT 0" +
            ");";

    String createVaultEntries = "CREATE TABLE IF NOT EXISTS vault_entries (" +
            "id INT AUTO_INCREMENT PRIMARY KEY, " +
            "user_id INT NOT NULL, " +
            "service_name VARCHAR(255) NOT NULL, " +
            "encrypted_username TEXT NOT NULL, " +
            "encrypted_password TEXT NOT NULL, " +
            "url TEXT, " +
            "notes TEXT, " +
            "last_modified TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
            "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ");";

    try (java.sql.Statement stmt = getConnection().createStatement()) {
        stmt.execute(createUsers);
        stmt.execute(createVaultEntries);
    } catch (SQLException e) {
        e.printStackTrace();
        System.err.println("Error initializing database: " + e.getMessage());
    }
}

}
