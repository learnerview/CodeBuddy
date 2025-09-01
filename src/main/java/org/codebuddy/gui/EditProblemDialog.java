package org.codebuddy.gui;

import org.codebuddy.core.dao.ProblemDao;
import org.codebuddy.core.models.*;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.util.List;

public class EditProblemDialog extends JDialog {
    private ProblemDao problemDao;
    private Problem problem;
    private boolean success = false;
    
    private JTextField nameField;
    private JComboBox<Platform> platformCombo;
    private JComboBox<Difficulty> difficultyCombo;
    private JTextField timeField;
    private JTextArea notesArea;
    private JTextField linkField;

    public EditProblemDialog(JFrame parent, int problemId) {
        super(parent, "Edit Problem", true);
        this.problemDao = new ProblemDao();
        
        loadProblem(problemId);
        initializeComponents();
        setupLayout();
        populateFields();
    }

    private void loadProblem(int problemId) {
        try {
            List<Problem> problems = problemDao.getAllProblems();
            for (Problem p : problems) {
                if (p.getId() == problemId) {
                    this.problem = p;
                    break;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading problem: " + e.getMessage());
        }
    }

    private void initializeComponents() {
        setSize(400, 500);
        setLocationRelativeTo(getParent());
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        nameField = new JTextField(20);
        platformCombo = new JComboBox<>(Platform.values());
        difficultyCombo = new JComboBox<>(Difficulty.values());
        timeField = new JTextField(10);
        notesArea = new JTextArea(5, 20);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        linkField = new JTextField(20);
    }

    private void setupLayout() {
        setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Problem Name
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Problem Name:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nameField, gbc);

        // Platform
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Platform:"), gbc);
        gbc.gridx = 1;
        formPanel.add(platformCombo, gbc);

        // Difficulty
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Difficulty:"), gbc);
        gbc.gridx = 1;
        formPanel.add(difficultyCombo, gbc);

        // Time Taken
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Time (minutes):"), gbc);
        gbc.gridx = 1;
        formPanel.add(timeField, gbc);

        // Link
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Problem Link:"), gbc);
        gbc.gridx = 1;
        formPanel.add(linkField, gbc);

        // Notes
        gbc.gridx = 0; gbc.gridy = 5;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        formPanel.add(new JLabel("Notes:"), gbc);
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.BOTH;
        formPanel.add(new JScrollPane(notesArea), gbc);

        add(formPanel, BorderLayout.CENTER);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton saveButton = new JButton("Update");
        JButton cancelButton = new JButton("Cancel");
        
        saveButton.addActionListener(e -> updateProblem());
        cancelButton.addActionListener(e -> dispose());
        
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void populateFields() {
        if (problem != null) {
            nameField.setText(problem.getName());
            platformCombo.setSelectedItem(problem.getPlatform());
            difficultyCombo.setSelectedItem(problem.getDifficulty());
            timeField.setText(String.valueOf(problem.getTimeTakenMin()));
            notesArea.setText(problem.getNotes());
            linkField.setText(problem.getLink());
        }
    }

    private void updateProblem() {
        try {
            // Validate input
            if (nameField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this, "Problem name is required!");
                return;
            }
            
            int timeTaken;
            try {
                timeTaken = Integer.parseInt(timeField.getText().trim());
                if (timeTaken <= 0) throw new NumberFormatException();
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid positive number for time taken!");
                return;
            }

            // Update problem object
            problem.setName(nameField.getText().trim());
            problem.setPlatform((Platform) platformCombo.getSelectedItem());
            problem.setDifficulty((Difficulty) difficultyCombo.getSelectedItem());
            problem.setTimeTakenMin(timeTaken);
            problem.setNotes(notesArea.getText().trim());
            problem.setLink(linkField.getText().trim());

            // Update in database
            problemDao.updateProblem(problem);
            
            success = true;
            JOptionPane.showMessageDialog(this, "Problem updated successfully!");
            dispose();
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error updating problem: " + e.getMessage());
        }
    }

    public boolean isSuccess() {
        return success;
    }
}
