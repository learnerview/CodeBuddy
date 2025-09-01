package org.codebuddy.core.models;

public enum Platform {
    LEETCODE("LeetCode"),
    CODEFORCES("Codeforces"),
    CODECHEF("CodeChef"),
    HACKERRANK("HackerRank"),
    OTHER("Other");

    private final String displayName;

    Platform(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }
}