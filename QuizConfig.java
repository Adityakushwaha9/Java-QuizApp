/**
*QuizConfig - Central configuration class for the Quiz App
* All file paths, settings, and constants are stored here
 * */

public class QuizConfig {
    //FILE PATHS AND LOCATIONS

    /** Main question file path */
    public static final String QUESTIONS_FILE = "src/questions.txt";

    /** High scores file(for future use) */
    public static final String HIGH_SCORES_FILE = "src/highScores.txt";

    //QUIZ SETTING AND BEHAVIOUR

    /** Maximum number of retry attempts for loading questions */
    public static final int MAX_RETRIES = 3;

    /** Delay between questions in milliseconds */
    public static final int DELAY_BETWEEN_QUESTIONS_MS = 2000;
    /** Safety limit - maximum number of questions to load */
    public static final int MAX_QUESTIONS = 50;
    /** Number of lines required per question in the file */
    public static final int LINES_PER_QUESTION = 6;

    //VALIDATION SETTINGS

    /** Minimum valid answer number */
    public static final int MIN_ANSWER = 1;
    /** Maximum valid answer number */
    public static final int MAX_ANSWER = 4;

    //MESSAGE AND TEXT CONTENT

    public static final String WELCOME_MESSAGE = "Welcome to the Java Quiz App!";
    public static final String CORRECT_MESSAGE = "Correct! Well done!";
    public static final String INCORRECT_MESSAGE = "Incorrect! The right answer was: ";
    public static final String LOADING_MESSAGE = "Loading quiz questions...";
    public static final String ATTEMPT_MESSAGE = "Attempt %d: Loading from %s";
    public static final String SUCCESS_MESSAGE = "Successfully loaded %d questions from file!";
    public static final String FOUND_QUESTIONS_MESSAGE = "Found %d questions in file. Validating ...";
    public static final String RETRY_COUNTDOWN =  "%d...";

    //ERROR MESSAGES

    public static final String ERROR_FILE_NOT_FOUND = "File not found: %s";
    public static final String ERROR_FILE_NOT_READAbLBE = "Cannot read file: %s";
    public static final String ERROR_EMPTY_FILE = "File is empty: %s";
    public static final String ERROR_FILE_FORMAT = "File format error: Incomplete question set. Each question needs exactly %d lines.";

    //Timer Settings
    public static final int QUESTION_TIMER_SECONDS=30;
    public static final String TIMER_MESSAGE= "Time remaining: %d seconds";
    public static final String TIME_UP_MESSAGE = "Time's up! Moving to next question...";

    //Score tracking settings
    public static final String SCORE_FILE = "scores.txt";
    public static final int MAX_SCORE_TO_KEEP = 5;
    public static final String HIGH_SCORE_MESSAGE = "New High Score!";
    public static final String SCORES_HEADER ="=== Quiz Scores ===";

    //UTILITY METHODS

    /**
     * Private constructor to prevent instantiation
     * This is a utility class with only static members
     */

    private QuizConfig(){
        throw new UnsupportedOperationException("QuizConfig is a utility class and cannot be instantiated");
    }
    /**
     * Gets the questions file path with automatic fallback
     * @return The best available file path for questions
     */

    public static String getQuestionsFilePath(){
        //first try the configuration path
        java.io.File file = new java.io.File(QUESTIONS_FILE);
        if(file.exists() && file.canRead()){
            return QUESTIONS_FILE;
        }
        // If not found in src/, try without src/ prefix (for different project structures)
        String altPath = QUESTIONS_FILE.replace("src/","");
        file = new java.io.File(altPath);
        if(file.exists() && file.canRead()){
            return altPath;
        }
        //Return the original configured path(will trigger error handling)
        return QUESTIONS_FILE;
    }
    /**
     * Validates if an answer is within the acceptable range
     * @param answer The answer to validate
     * @return true if valid, false otherwise
     */
    public static boolean isValidAnswer(int answer){
        return answer>= MIN_ANSWER && answer<= MAX_ANSWER;
    }

    /**
     * Gets the valid answer range as a display string
     * @return String like "1-4"
     */
    public static String getAnswerRange(){
        return MIN_ANSWER + "-" + MAX_ANSWER;
    }
}