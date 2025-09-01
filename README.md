# CodeBuddy – Competitive Practice Tracker

A simple Java desktop application for tracking competitive programming practice using **Java**, **JDBC**, **MySQL**, and **Swing** with minimal UI complexity.

## Features

- **Problem Management**: Add, edit, delete problems with platform, difficulty, time tracking
- **Tag Filtering**: Filter by platform (LeetCode, CodeChef, CodeForces, HackerRank, Other) and difficulty
- **Weak-topic Detection**: View difficulty distribution to identify areas for improvement  
- **Performance Analytics**: Track problem counts, streaks, and average solving time
- **Simple GUI**: Basic Swing interface with tables and dialogs

## Technology Stack

- **Backend**: Java 17, JDBC, MySQL
- **Frontend**: Java Swing (minimal UI)
- **Architecture**: OOP principles, DAO pattern
- **Build**: Maven

## Prerequisites

1. **Java 17+**
2. **MySQL 8.0+** 
3. **Maven 3.6+**

## Setup & Installation

### 1. Configure MySQL
```sql
-- Connect to MySQL as root
mysql -u root -p

-- Create database
CREATE DATABASE codebuddy_db;
EXIT;
```

### 2. Update Database Configuration
Edit `src/main/resources/config.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/codebuddy_db
db.user=root
db.password=your_mysql_password
```

### 3. Build and Run
```bash
# Compile the project
mvn clean compile

# Run the application
mvn exec:java
```

## Usage

1. **Launch**: Run `mvn exec:java` - database tables are created automatically
2. **Add Problems**: Click "Add Problem" button, fill details, and save
3. **Filter**: Use dropdown filters for platform and difficulty
4. **Analytics**: Click "View Analytics" for performance insights
5. **Edit/Delete**: Select a problem from table and use respective buttons

## Project Structure

```
src/main/java/org/codebuddy/
├── Main.java                    # Application entry point
├── core/
│   ├── dao/
│   │   ├── DatabaseManager.java # Database connection & setup
│   │   └── ProblemDao.java      # Problem data access
│   ├── models/
│   │   ├── Problem.java         # Problem entity
│   │   ├── Platform.java        # Platform enum
│   │   └── Difficulty.java      # Difficulty enum
│   └── services/
│       └── AnalyticsService.java # Analytics calculations
└── gui/
    ├── SwingApp.java            # GUI application launcher
    ├── SwingMainWindow.java     # Main window
    ├── AddProblemDialog.java    # Add problem dialog
    ├── EditProblemDialog.java   # Edit problem dialog
    └── AnalyticsDialog.java     # Analytics display
```

## Database Schema

```sql
CREATE TABLE problems (
    problem_id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    platform VARCHAR(50) NOT NULL,
    difficulty VARCHAR(20) NOT NULL,
    time_taken_min INT NOT NULL,
    solved_date DATETIME NOT NULL,
    notes TEXT,
    link TEXT,
    UNIQUE KEY unique_problem (name, platform, difficulty)
);
```

## Key Concepts Used

- **OOP**: Classes, inheritance, encapsulation
- **JDBC**: Database connectivity and operations
- **DAO Pattern**: Data access abstraction
- **Swing**: Basic GUI components and event handling
- **Enums**: Type-safe constants for Platform and Difficulty
- **Exception Handling**: Proper error management 
