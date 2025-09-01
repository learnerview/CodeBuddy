package org.codebuddy.gui;

import org.codebuddy.core.dao.DatabaseManager;

import javax.swing.*;
import java.sql.SQLException;

public class SwingApp {
    
    public static void main(String[] args) {
        // Initialize database
        try {
            DatabaseManager.initializeDatabase();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, 
                "Failed to initialize database: " + e.getMessage() + 
                "\n\nPlease ensure MySQL is running and accessible.", 
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }

        // Launch main window
        SwingUtilities.invokeLater(() -> {
            new SwingMainWindow().setVisible(true);
        });
    }
}
