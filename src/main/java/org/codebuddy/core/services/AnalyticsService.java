package org.codebuddy.core.services;

import org.codebuddy.core.dao.ProblemDao;
import org.codebuddy.core.models.Problem;
import java.sql.SQLException;
import java.util.*;

public class AnalyticsService {
    private final ProblemDao problemDao = new ProblemDao();

    public Map<String, Object> getAnalytics(int userId) throws SQLException {
        List<Problem> problems = problemDao.getAllProblemsForUser(userId);
        Map<String, Object> analytics = new HashMap<>();
        
        // Total problems
        analytics.put("totalProblems", problems.size());
        
        // Current and max streak
        analytics.put("currentStreak", problemDao.getCurrentStreak(userId));
        analytics.put("maxStreak", calculateMaxStreak(problems));
        
        // Platform distribution
        Map<String, Integer> platformDist = new HashMap<>();
        for (Problem p : problems) {
            String platform = p.getPlatform().getDisplayName();
            platformDist.put(platform, platformDist.getOrDefault(platform, 0) + 1);
        }
        analytics.put("platformDistribution", platformDist);
        
        // Difficulty distribution
        Map<String, Integer> difficultyDist = new HashMap<>();
        for (Problem p : problems) {
            String difficulty = p.getDifficulty().getDisplayName();
            difficultyDist.put(difficulty, difficultyDist.getOrDefault(difficulty, 0) + 1);
        }
        analytics.put("difficultyDistribution", difficultyDist);
        
        // Average time
        if (!problems.isEmpty()) {
            double avgTime = problems.stream().mapToInt(Problem::getTimeTakenMin).average().orElse(0.0);
            analytics.put("averageTime", avgTime);
        }
        
        return analytics;
    }
    
    private int calculateMaxStreak(List<Problem> problems) {
        if (problems.isEmpty()) return 0;
        
        Set<String> uniqueDates = new HashSet<>();
        for (Problem p : problems) {
            uniqueDates.add(p.getSolvedDate().toLocalDate().toString());
        }
        
        List<String> sortedDates = new ArrayList<>(uniqueDates);
        Collections.sort(sortedDates);
        
        int maxStreak = 1;
        int currentStreak = 1;
        
        for (int i = 1; i < sortedDates.size(); i++) {
            String prevDate = sortedDates.get(i - 1);
            String currDate = sortedDates.get(i);
            
            // Check if dates are consecutive
            if (java.time.LocalDate.parse(currDate).minusDays(1).toString().equals(prevDate)) {
                currentStreak++;
                maxStreak = Math.max(maxStreak, currentStreak);
            } else {
                currentStreak = 1;
            }
        }
        
        return maxStreak;
    }
}