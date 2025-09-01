package org.codebuddy.gui;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class AnalyticsDialog extends JDialog {
    private Map<String, Object> analytics;

    public AnalyticsDialog(JFrame parent, Map<String, Object> analytics) {
        super(parent, "Analytics Dashboard", true);
        this.analytics = analytics;
        
        initializeComponents();
        setupLayout();
    }

    private void initializeComponents() {
        setSize(500, 400);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }

    @SuppressWarnings("unchecked")
    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Total Problems
        Integer totalProblems = (Integer) analytics.get("totalProblems");
        contentPanel.add(createStatLabel("Total Problems Solved: " + (totalProblems != null ? totalProblems : 0)));
        contentPanel.add(Box.createVerticalStrut(10));

        // Current Streak
        Integer currentStreak = (Integer) analytics.get("currentStreak");
        contentPanel.add(createStatLabel("Current Streak: " + (currentStreak != null ? currentStreak : 0) + " days"));
        contentPanel.add(Box.createVerticalStrut(10));

        // Max Streak
        Integer maxStreak = (Integer) analytics.get("maxStreak");
        contentPanel.add(createStatLabel("Maximum Streak: " + (maxStreak != null ? maxStreak : 0) + " days"));
        contentPanel.add(Box.createVerticalStrut(15));

        // Platform Distribution
        contentPanel.add(createSectionLabel("Platform Distribution:"));
        Map<String, Integer> platformStats = (Map<String, Integer>) analytics.get("platformDistribution");
        if (platformStats != null) {
            for (Map.Entry<String, Integer> entry : platformStats.entrySet()) {
                contentPanel.add(createStatLabel("  " + entry.getKey() + ": " + entry.getValue() + " problems"));
            }
        }
        contentPanel.add(Box.createVerticalStrut(15));

        // Difficulty Distribution
        contentPanel.add(createSectionLabel("Difficulty Distribution:"));
        Map<String, Integer> difficultyStats = (Map<String, Integer>) analytics.get("difficultyDistribution");
        if (difficultyStats != null) {
            for (Map.Entry<String, Integer> entry : difficultyStats.entrySet()) {
                contentPanel.add(createStatLabel("  " + entry.getKey() + ": " + entry.getValue() + " problems"));
            }
        }
        contentPanel.add(Box.createVerticalStrut(15));

        // Average Time
        Double avgTime = (Double) analytics.get("averageTime");
        if (avgTime != null) {
            contentPanel.add(createStatLabel("Average Time per Problem: " + String.format("%.1f", avgTime) + " minutes"));
        }

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        add(scrollPane, BorderLayout.CENTER);

        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dispose());
        buttonPanel.add(closeButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JLabel createSectionLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(Font.BOLD, 14f));
        return label;
    }

    private JLabel createStatLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(label.getFont().deriveFont(12f));
        return label;
    }
}
