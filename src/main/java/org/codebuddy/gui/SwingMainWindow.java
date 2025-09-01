package org.codebuddy.gui;

import org.codebuddy.core.dao.ProblemDao;
import org.codebuddy.core.models.*;
import org.codebuddy.core.services.AnalyticsService;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class SwingMainWindow extends JFrame {
    private ProblemDao problemDao;
    private AnalyticsService analyticsService;
    private DefaultTableModel tableModel;
    private JTable problemTable;
    private JComboBox<Platform> platformFilter;
    private JComboBox<Difficulty> difficultyFilter;
    private JLabel statsLabel;
    
    // Simplified - no user management needed
    private final int currentUserId = 1;

    public SwingMainWindow() {
        this.problemDao = new ProblemDao();
        this.analyticsService = new AnalyticsService();
        
        initializeComponents();
        setupLayout();
        loadProblems();
        updateStats();
    }

    private void initializeComponents() {
        setTitle("CodeBuddy - Competitive Practice Tracker");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        // Create table model
        String[] columnNames = {"ID", "Problem Name", "Platform", "Difficulty", "Time (min)", "Date", "Notes"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        problemTable = new JTable(tableModel);
        problemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Create filters
        platformFilter = new JComboBox<>();
        platformFilter.addItem(null); // "All" option
        for (Platform platform : Platform.values()) {
            platformFilter.addItem(platform);
        }
        
        difficultyFilter = new JComboBox<>();
        difficultyFilter.addItem(null); // "All" option
        for (Difficulty difficulty : Difficulty.values()) {
            difficultyFilter.addItem(difficulty);
        }
        
        // Stats label
        statsLabel = new JLabel("Loading stats...");
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        // Top panel with filters and buttons
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Platform:"));
        topPanel.add(platformFilter);
        topPanel.add(new JLabel("Difficulty:"));
        topPanel.add(difficultyFilter);
        
        JButton addButton = new JButton("Add Problem");
        JButton editButton = new JButton("Edit Problem");
        JButton deleteButton = new JButton("Delete Problem");
        JButton refreshButton = new JButton("Refresh");
        JButton analyticsButton = new JButton("View Analytics");
        
        topPanel.add(addButton);
        topPanel.add(editButton);
        topPanel.add(deleteButton);
        topPanel.add(refreshButton);
        topPanel.add(analyticsButton);
        
        add(topPanel, BorderLayout.NORTH);

        // Center panel with table
        JScrollPane scrollPane = new JScrollPane(problemTable);
        add(scrollPane, BorderLayout.CENTER);

        // Bottom panel with stats
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        bottomPanel.add(statsLabel);
        add(bottomPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> showAddProblemDialog());
        editButton.addActionListener(e -> showEditProblemDialog());
        deleteButton.addActionListener(e -> deleteProblem());
        refreshButton.addActionListener(e -> {
            loadProblems();
            updateStats();
        });
        analyticsButton.addActionListener(e -> showAnalytics());
        
        platformFilter.addActionListener(e -> loadProblems());
        difficultyFilter.addActionListener(e -> loadProblems());
    }

    private void loadProblems() {
        try {
            List<Problem> problems = problemDao.getAllProblemsForUser(currentUserId);
            
            // Apply filters
            Platform selectedPlatform = (Platform) platformFilter.getSelectedItem();
            Difficulty selectedDifficulty = (Difficulty) difficultyFilter.getSelectedItem();
            
            // Clear table
            tableModel.setRowCount(0);
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            
            for (Problem problem : problems) {
                if (selectedPlatform != null && !problem.getPlatform().equals(selectedPlatform)) {
                    continue;
                }
                if (selectedDifficulty != null && !problem.getDifficulty().equals(selectedDifficulty)) {
                    continue;
                }
                
                Object[] row = {
                    problem.getId(),
                    problem.getName(),
                    problem.getPlatform().getDisplayName(),
                    problem.getDifficulty().getDisplayName(),
                    problem.getTimeTakenMin(),
                    problem.getSolvedDate().format(formatter),
                    problem.getNotes()
                };
                tableModel.addRow(row);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading problems: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateStats() {
        try {
            int totalProblems = problemDao.getAllProblemsForUser(currentUserId).size();
            int todayProblems = problemDao.getProblemsSolvedToday(currentUserId);
            int currentStreak = problemDao.getCurrentStreak(currentUserId);
            
            statsLabel.setText(String.format("Total Problems: %d | Today: %d | Current Streak: %d days", 
                              totalProblems, todayProblems, currentStreak));
        } catch (SQLException e) {
            statsLabel.setText("Error loading stats");
        }
    }

    private void showAddProblemDialog() {
        AddProblemDialog dialog = new AddProblemDialog(this, currentUserId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadProblems();
            updateStats();
        }
    }

    private void showEditProblemDialog() {
        int selectedRow = problemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a problem to edit.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int problemId = (Integer) tableModel.getValueAt(selectedRow, 0);
        EditProblemDialog dialog = new EditProblemDialog(this, problemId);
        dialog.setVisible(true);
        if (dialog.isSuccess()) {
            loadProblems();
            updateStats();
        }
    }

    private void deleteProblem() {
        int selectedRow = problemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a problem to delete.", 
                                        "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int result = JOptionPane.showConfirmDialog(this, 
                                                 "Are you sure you want to delete this problem?", 
                                                 "Confirm Delete", 
                                                 JOptionPane.YES_NO_OPTION);
        if (result == JOptionPane.YES_OPTION) {
            try {
                int problemId = (Integer) tableModel.getValueAt(selectedRow, 0);
                problemDao.deleteProblem(problemId);
                loadProblems();
                updateStats();
                JOptionPane.showMessageDialog(this, "Problem deleted successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this, "Error deleting problem: " + e.getMessage(), 
                                            "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showAnalytics() {
        try {
            Map<String, Object> analytics = analyticsService.getAnalytics(currentUserId);
            AnalyticsDialog dialog = new AnalyticsDialog(this, analytics);
            dialog.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading analytics: " + e.getMessage(), 
                                        "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
