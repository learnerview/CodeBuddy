package org.codebuddy.core.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;
import java.util.Properties;

public class DatabaseManager {
    private static final String CONFIG_FILE = "/config.properties";
    private static final String DEFAULT_DB_URL = "jdbc:mysql://localhost:3306/codebuddy_db";
    private static final String DEFAULT_DB_USER = "root";
    private static final String DEFAULT_DB_PASSWORD = "123321";
    
    private static Connection connection;
    private static boolean initialized = false;

    private DatabaseManager() {}

    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                Properties props = loadDatabaseProperties();
                String dbUrl = props.getProperty("db.url", DEFAULT_DB_URL);
                String dbUser = props.getProperty("db.user", DEFAULT_DB_USER);
                String dbPassword = props.getProperty("db.password", DEFAULT_DB_PASSWORD);
                
                // Ensure database exists
                ensureDatabaseExists(dbUrl, dbUser, dbPassword);
                
                // Connect to the database
                connection = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
                connection.setAutoCommit(true);
                
            } catch (IOException e) {
                throw new SQLException("Failed to load database config: " + e.getMessage(), e);
            }
        }
        return connection;
    }

    private static Properties loadDatabaseProperties() throws IOException {
        Properties props = new Properties();
        try (InputStream input = DatabaseManager.class.getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            }
        }
        return props;
    }

    private static void ensureDatabaseExists(String dbUrl, String dbUser, String dbPassword) throws SQLException {
        // Extract database name from URL
        String dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1);
        String serverUrl = dbUrl.substring(0, dbUrl.lastIndexOf("/"));
        
        // Connect to MySQL server (without specifying database)
        try (Connection serverConn = DriverManager.getConnection(serverUrl, dbUser, dbPassword);
             Statement stmt = serverConn.createStatement()) {
            
            // Create database if it doesn't exist
            stmt.executeUpdate("CREATE DATABASE IF NOT EXISTS `" + dbName + "`");
            System.out.println("Database '" + dbName + "' is ready");
        }
    }

    public static void initializeDatabase() throws SQLException {
        if (initialized) {
            return;
        }
        
        createTables();
        addSampleProblems();
        
        initialized = true;
        System.out.println("Database initialized successfully");
    }

    private static void createTables() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            
            // Drop existing tables to ensure clean schema
            stmt.executeUpdate("DROP TABLE IF EXISTS problems");
            stmt.executeUpdate("DROP TABLE IF EXISTS users");
            
            // Create problems table without user_id
            String createProblemsTable = """
                CREATE TABLE problems (
                    problem_id INT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    platform VARCHAR(50) NOT NULL,
                    difficulty VARCHAR(20) NOT NULL,
                    time_taken_min INT,
                    solved_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    notes TEXT,
                    link VARCHAR(500)
                )
                """;
            
            stmt.executeUpdate(createProblemsTable);
            System.out.println("Database tables created successfully");
        }
    }

    private static void addSampleProblems() throws SQLException {
        try (Connection conn = getConnection(); Statement stmt = conn.createStatement()) {
            // Add sample problems if not present
            try {
                stmt.executeUpdate("INSERT INTO problems (name, platform, difficulty, time_taken_min, solved_date, notes, link) VALUES " +
                        "('Two Sum', 'LEETCODE', 'EASY', 15, NOW(), 'Classic hashmap problem', 'https://leetcode.com/problems/two-sum/')");
                stmt.executeUpdate("INSERT INTO problems (name, platform, difficulty, time_taken_min, solved_date, notes, link) VALUES " +
                        "('Median of Two Sorted Arrays', 'LEETCODE', 'HARD', 60, NOW(), 'Binary search required', 'https://leetcode.com/problems/median-of-two-sorted-arrays/')");
                stmt.executeUpdate("INSERT INTO problems (name, platform, difficulty, time_taken_min, solved_date, notes, link) VALUES " +
                        "('Chef and Strings', 'CODECHEF', 'MEDIUM', 25, NOW(), 'String manipulation', 'https://www.codechef.com/problems/STRINGS')");
                System.out.println("Sample problems added");
            } catch (SQLException e) {
                // Problems already exist, ignore
                System.out.println("Sample problems already exist");
            }

            initialized = true;
            System.out.println("Database initialized successfully");
            
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            throw e;
        }
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                initialized = false;
            } catch (SQLException e) {
                System.err.println("Failed to close connection: " + e.getMessage());
            }
        }
    }

    public static boolean isInitialized() {
        return initialized;
    }
}