# Java Quiz Application 🎯

A feature-rich, console-based quiz application built with Java that demonstrates professional software development practices including robust error handling, configuration management, and modular architecture.

## ✨ Features

### 🎮 Core Quiz Features
- *Multiple-choice questions* with 4 options
- *Dynamic question loading* from external files
- *Real-time scoring* with percentage calculation
- *Smooth user experience* with timed transitions between questions
- *Progress tracking* throughout the quiz
- *⏰ 30-second timer per question* with real-time countdown
- *🏆 Persistent score tracking* with history and high scores

### 🛡 Robust Error Handling
- *Comprehensive input validation* - handles invalid user inputs gracefully
- *File system resilience* - automatic recovery from missing/corrupted files
- *Smart retry mechanism* - 3 attempts with user assistance for file loading
- *Graceful degradation* - never crashes, always provides helpful error messages

### 📊 Score & Performance Tracking
- *Last 5 scores saved* with timestamps
- *All-time high score tracking*
- *Performance feedback* based on score percentage
- *Persistent score storage* across application restarts
- *Score history display* at quiz completion

### ⚙ Professional Architecture
- *Configuration-driven design* - easy customization via QuizConfig.java
- *Modular code structure* - separation of concerns with dedicated classes
- *Object-Oriented Principles* - proper encapsulation and method organization
- *Professional logging* - informative messages with emoji visual cues
- *Multi-threaded timer* - non-blocking user experience

## 🚀 Quick Start

### Prerequisites
- Java 8 or higher
- Any terminal/command prompt

### Installation & Running
1. *Clone the repository*
   ```bash
   git clone https://github.com/Adityakushwaha9/java-quizapp.git
   cd java-quizapp