package org.codebuddy.core.dao;

import org.codebuddy.core.models.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProblemDao {
    public void saveProblem(Problem problem) throws SQLException {
        String sql = "INSERT INTO problems (name, platform, difficulty, time_taken_min, solved_date, notes, link) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setString(1, problem.getName());
            stmt.setString(2, problem.getPlatform().name());
            stmt.setString(3, problem.getDifficulty().name());
            stmt.setInt(4, problem.getTimeTakenMin());
            stmt.setTimestamp(5, Timestamp.valueOf(problem.getSolvedDate()));
            stmt.setString(6, problem.getNotes());
            stmt.setString(7, problem.getLink());
            
            stmt.executeUpdate();
            
            // Get the generated ID
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    problem.setId(rs.getInt(1));
                }
            }
        }
    }

    public void updateProblem(Problem problem) throws SQLException {
        String sql = "UPDATE problems SET name=?, platform=?, difficulty=?, time_taken_min=?, notes=?, link=? WHERE problem_id=?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, problem.getName());
            stmt.setString(2, problem.getPlatform().name());
            stmt.setString(3, problem.getDifficulty().name());
            stmt.setInt(4, problem.getTimeTakenMin());
            stmt.setString(5, problem.getNotes());
            stmt.setString(6, problem.getLink());
            stmt.setInt(7, problem.getId());
            stmt.executeUpdate();
        }
    }

    public void deleteProblem(int problemId) throws SQLException {
        String sql = "DELETE FROM problems WHERE problem_id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, problemId);
            stmt.executeUpdate();
        }
    }

    public List<Problem> getAllProblems() throws SQLException {
        String sql = "SELECT * FROM problems ORDER BY solved_date DESC";
        List<Problem> problems = new ArrayList<>();

        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                problems.add(createProblemFromResultSet(rs));
            }
        }
        return problems;
    }

    // Simplified - no user management needed
    public List<Problem> getAllProblemsForUser(int userId) throws SQLException {
        return getAllProblems();
    }

    public int getProblemsSolvedToday(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM problems WHERE DATE(solved_date) = CURDATE()";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public int getCurrentStreak(int userId) throws SQLException {
        String sql = "SELECT DISTINCT DATE(solved_date) as solved_date FROM problems ORDER BY solved_date DESC";
        try (Connection conn = DatabaseManager.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int streak = 0;
            java.time.LocalDate today = java.time.LocalDate.now();
            java.time.LocalDate currentDate = today;
            
            while (rs.next()) {
                java.time.LocalDate date = rs.getDate("solved_date").toLocalDate();
                if (date.equals(currentDate)) {
                    streak++;
                    currentDate = currentDate.minusDays(1);
                } else {
                    break;
                }
            }
            return streak;
        }
    }

    private Problem createProblemFromResultSet(ResultSet rs) throws SQLException {
        try {
            return new Problem(
                rs.getInt("problem_id"),
                rs.getString("name"),
                Platform.valueOf(rs.getString("platform")),
                Difficulty.valueOf(rs.getString("difficulty")),
                rs.getInt("time_taken_min"),
                rs.getTimestamp("solved_date").toLocalDateTime(),
                rs.getString("notes"),
                rs.getString("link")
            );
        } catch (IllegalArgumentException e) {
            // Handle legacy data with old format
            String platformStr = rs.getString("platform");
            String difficultyStr = rs.getString("difficulty");
            
            Platform platform = mapLegacyPlatform(platformStr);
            Difficulty difficulty = mapLegacyDifficulty(difficultyStr);
            
            return new Problem(
                rs.getInt("problem_id"),
                rs.getString("name"),
                platform,
                difficulty,
                rs.getInt("time_taken_min"),
                rs.getTimestamp("solved_date").toLocalDateTime(),
                rs.getString("notes"),
                rs.getString("link")
            );
        }
    }

    private Platform mapLegacyPlatform(String platformStr) {
        if (platformStr == null) return Platform.OTHER;
        
        switch (platformStr.toLowerCase()) {
            case "leetcode": return Platform.LEETCODE;
            case "codeforces": return Platform.CODEFORCES;
            case "codechef": return Platform.CODECHEF;
            case "hackerrank": return Platform.HACKERRANK;
            default: return Platform.OTHER;
        }
    }

    private Difficulty mapLegacyDifficulty(String difficultyStr) {
        if (difficultyStr == null) return Difficulty.MEDIUM;
        
        switch (difficultyStr.toLowerCase()) {
            case "easy": return Difficulty.EASY;
            case "medium": return Difficulty.MEDIUM;
            case "hard": return Difficulty.HARD;
            default: return Difficulty.MEDIUM;
        }
    }
}