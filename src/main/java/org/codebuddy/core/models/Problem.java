package org.codebuddy.core.models;

import java.time.LocalDateTime;
import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a competitive programming problem solved by a user.
 */
public class Problem implements Serializable {
    private int id;
    private String name;
    private Platform platform;
    private Difficulty difficulty;
    private int timeTakenMin;
    private LocalDateTime solvedDate;
    private String notes;
    private String link;

    public Problem() {
        // No-argument constructor for placeholder rows
    }

    public Problem(int id, String name, Platform platform, Difficulty difficulty, int timeTakenMin, LocalDateTime solvedDate, String notes, String link) {
        this.id = id;
        this.name = name;
        this.platform = platform;
        this.difficulty = difficulty;
        this.timeTakenMin = timeTakenMin;
        this.solvedDate = solvedDate;
        this.notes = notes;
        this.link = link;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Platform getPlatform() { return platform; }
    public void setPlatform(Platform platform) { this.platform = platform; }
    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }
    public int getTimeTakenMin() { return timeTakenMin; }
    public void setTimeTakenMin(int timeTakenMin) { this.timeTakenMin = timeTakenMin; }
    public LocalDateTime getSolvedDate() { return solvedDate; }
    public void setSolvedDate(LocalDateTime solvedDate) { this.solvedDate = solvedDate; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Problem problem = (Problem) o;
        return id == problem.id &&
                timeTakenMin == problem.timeTakenMin &&
                Objects.equals(name, problem.name) &&
                platform == problem.platform &&
                difficulty == problem.difficulty &&
                Objects.equals(solvedDate, problem.solvedDate) &&
                Objects.equals(notes, problem.notes) &&
                Objects.equals(link, problem.link);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, platform, difficulty, timeTakenMin, solvedDate, notes, link);
    }

    @Override
    public String toString() {
        return "Problem{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", platform=" + platform +
                ", difficulty=" + difficulty +
                ", timeTakenMin=" + timeTakenMin +
                ", solvedDate=" + solvedDate +
                ", notes='" + notes + '\'' +
                ", link='" + link + '\'' +
                '}';
    }
}