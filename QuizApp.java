import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
public class QuizApp {
   static class color{
        static String RESET = "\u001B[0m";
        static String YELLOW = "\u001B[43m";
        static String BLUE = "\u001B[34m";
    }
    //using array to store questions
    private Question[] questions =new Question[0];
    private int score;
    private final Scanner scanner;
    private volatile boolean timeUp = false;


    public QuizApp(){
        this.scanner=new Scanner(System.in);
        this.score=0;
        initializeQuestions();
    }

    private void loadDefaultQuestions(){
        questions =new Question[3];

        questions[0]=new Question(
        "What is the capital of France?",
        new String[]{"1. London","2. Paris", "3. Berlin", "4. Rome"},
        2
        );

        questions[1]=new Question(
                "What is 2+2?",
                new String[]{"1. 3","2. 4", "3. 5", "4. 6"},
                2
        );

        questions[2]=new Question(
                "Which programming language is this quiz written in?",
                new String[]{"1. Java","2. Python", "3. C++", "4. JavaScript"},
                1
        );
   }

   private void initializeQuestions(){
        int maxRetries =QuizConfig.MAX_RETRIES;
        int attempt = 0;

       System.out.println(QuizConfig.LOADING_MESSAGE);
       while(attempt < maxRetries){
        try{
        if(attempt ==0) {
            //  java.nio.file.Path currentDir = java.nio.file.Paths.get(".").toAbsolutePath();
            // System.out.println(currentDir);
            // java.nio.file.Path filePath = java.nio.file.Paths.get("questions.txt").toAbsolutePath();
            // System.out.println("path"+ filePath);
            /* Use the config class to get the file path */
            String filePath = QuizConfig.getQuestionsFilePath();
            System.out.println(String.format(QuizConfig.ATTEMPT_MESSAGE, (attempt + 1) , filePath));
            loadQuestionFromFile(filePath);
            System.out.println(String.format(QuizConfig.SUCCESS_MESSAGE, questions.length));
            return;
        }else{
            //Subsequent tries: ask user for help
            System.out.println("\n--- Attempt "+ (attempt+1)+ "of "+ maxRetries+ " ---");
            System.out.println("The quiz questions couldn't be loaded automatically.");
            System.out.println("You can:");
            System.out.println("1. Enter a different file path");
            System.out.println("2. Press enter to use built-in  questions");
            System.out.println("Your choice: ");

            String userChoice = scanner.nextLine().trim();

            if(userChoice.isEmpty()){
                /* User pressed enter -use default questions */
                System.out.println("Using built-in questions...");
                loadDefaultQuestions();
                return;
            }else{
                //User entered a custom file path
                System.out.println("Loading from: "+ userChoice);
                loadQuestionFromFile(userChoice);
                System.out.println("Successfully loaded questions from custom path!");
                return;
            }
        }
        } catch(Exception e){
            attempt++;
            System.out.println("Attempt " + attempt+ " failed: "+ e.getMessage());

            if(attempt < maxRetries){
                System.out.println("Retrying in 2 seconds...");
                //show a countdown using config
                try{
                    for(int i=2; i>0; i--){
                        System.out.println(String.format(QuizConfig.RETRY_COUNTDOWN,i));
                        Thread.sleep(1000);
                    }
                    System.out.println();
                }catch(InterruptedException ie){
                    Thread.currentThread().interrupt();
                }
            }else{
                //All attempts failed
                System.out.println("\n All attempt failed. Using built-in questions.");
                System.out.println("You can fix the file and restart the quiz later.");
                loadDefaultQuestions();
            }
        }
          /*  System.out.println("Error loading questions: "+ e.getMessage());
            System.out.println("Using default questions instead...");
            loadDefaultQuestions(); */
        }
    }

    private void loadQuestionFromFile(String filename){
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filename);

            //check if file exists
            if(!java.nio.file.Files.exists(path)){
                throw new RuntimeException(String.format(QuizConfig.ERROR_FILE_NOT_FOUND, filename));
            }

            //check if file is readable
            if(!java.nio.file.Files.isReadable(path)){
                throw new RuntimeException(String.format(QuizConfig.ERROR_FILE_NOT_READAbLBE,filename));
            }

            //read all lines from file
            java.util.List<String> allLines = java.nio.file.Files.readAllLines(path);

            //check if file is empty
            if(allLines.isEmpty()){
                throw new RuntimeException(String.format(QuizConfig.ERROR_EMPTY_FILE,filename));
            }

            //check if we have complete sets of 6 lines per question using config constant
            if(allLines.size()% QuizConfig.LINES_PER_QUESTION !=0){
                throw new RuntimeException(String.format(QuizConfig.ERROR_FILE_FORMAT, QuizConfig.LINES_PER_QUESTION));
            }

            int questionCount = allLines.size() / QuizConfig.LINES_PER_QUESTION;
            //Safety check using config
            if(questionCount > QuizConfig.MAX_QUESTIONS){
                System.out.println("Warning: file has " + questionCount + " questions, limiting to " + QuizConfig.MAX_QUESTIONS);
                questionCount = QuizConfig.MAX_QUESTIONS;
            }

            questions = new Question[questionCount];
            System.out.println(String.format(QuizConfig.FOUND_QUESTIONS_MESSAGE,questionCount));

            for (int i = 0; i < questionCount; i++) {
                int startIndex = i * QuizConfig.LINES_PER_QUESTION;
                //validate this question's format before creating it
                validateQuestionFormat(allLines, startIndex, i+1);
                String[] linesArray = allLines.toArray(new String[0]);
                questions[i] = Question.fromStringArray(linesArray, startIndex);
            }

          //  System.out.println("Successfully loaded "+ questions.length + " question from file!");
        } catch (java.nio.file.NoSuchFileException e) {
            throw new RuntimeException(String.format(QuizConfig.ERROR_FILE_NOT_FOUND,filename));
        } catch(java.nio.file.AccessDeniedException e){
            throw new RuntimeException("Access Denied to file: "+filename);
        } catch (Exception e) {
            throw new RuntimeException("Error reading file: "+ e.getMessage());
        }
    }

    private void  validateQuestionFormat(java.util.List<String> lines, int startIndex, int questionNumber){
        try{
             //check if we have enough lines for this question
            if(startIndex+5>= lines.size()){
                throw new RuntimeException("Not enough lines for question "+questionNumber);
            }

            //check question text(line 1)
            String questionText = lines.get(startIndex);
            if(questionText == null || questionText.trim().isEmpty()){
               throw new RuntimeException("Empty option text for question "+ questionNumber);
            }

            //check all four options(lines 2-5)
            for(int i =1; i<=4; i++){
                String option = lines.get(startIndex+i);
                if(option==null || option.trim().isEmpty()){
                    throw new RuntimeException("Empty option "+ i + " for question "+ questionNumber);
                }
            }

            //check correct answer(lines 6)
            String answerStr = lines.get(startIndex+5);
            if(answerStr ==null || answerStr.trim().isEmpty()){
                throw new RuntimeException("Empty answer for question "+ questionNumber);
            }

            //Try to parse the answer as integer
            try{
                int answer = Integer.parseInt(answerStr.trim());
                if(answer<1 || answer >4){
                    throw new RuntimeException("Invalid answer "+ answer + " for question "+ questionNumber+ ". Must be 1-4");
                }
            }catch(NumberFormatException e){
                throw new RuntimeException("Answer is not a number for question " + questionNumber + ": "+ answerStr);
            }
        } catch(IndexOutOfBoundsException e){
            throw new RuntimeException("incomplete question data for question "  + questionNumber);
        }
    }

    public void startQuiz(){
        displayWelcomeMessage();

        for(int i=0;i<questions.length;i++){
            Question currentQuestion= questions[i];
            currentQuestion.displayQuestion(i+1);

         //   int userAnswer = getUserAnswer();
            int userAnswer = getAnswerWithTimer(currentQuestion);
            checkAnswer(currentQuestion,userAnswer);

           if(i< questions.length-1) {
              // showLoadingAnimation();
              try {
                   System.out.print(color.YELLOW+"\u001B[30m"+"Loading next Question" +color.RESET);
                   Thread.sleep(1000);
                   System.out.print(color.BLUE+".");
                   Thread.sleep(1000);
                   System.out.print(".");
                   Thread.sleep(1000);
                   System.out.println("." + color.RESET);
                   Thread.sleep(1000);
               } catch (InterruptedException e) {
                   throw new RuntimeException(e);
               }
           }
        }
        saveScore();
        showFinalResults();
    }

    private int getAnswerWithTimer(Question question){
        timeUp = false;
        AtomicInteger userAnswer = new AtomicInteger(-1);
        AtomicBoolean answerGiven = new AtomicBoolean(false);

        //Timer thread
        Thread timerThread = new Thread(() -> {
           int timeLeft = QuizConfig.QUESTION_TIMER_SECONDS;

           try {
               while (timeLeft > 0 && !answerGiven.get()) {
                   //Clear line and show timer
                   //   System.out.print("\r" + " ".repeat(50) + "\r");
                   System.out.print(String.format("\r" +QuizConfig.TIMER_MESSAGE, timeLeft).repeat(10));
                   Thread.sleep(1000);
                   timeLeft--;
               }

               if (!answerGiven.get()) {
                   timeUp = true;
                   System.out.println("\r" + QuizConfig.TIME_UP_MESSAGE + " ".repeat(20));
               }
           }  catch(InterruptedException e){
               System.out.print("\r"+ " ".repeat(50) + "\r");
            }
        });

        //Answer input thread
        Thread inputThread = new Thread(() ->{
            System.out.println("\nYour answer("  +QuizConfig.getAnswerRange()+ "): ");

            while(!timeUp && !answerGiven.get()){
                if(scanner.hasNextLine()){
                    String input = scanner.nextLine().trim();

                    if(!input.isEmpty()){
                        try{
                            int answer = Integer.parseInt(input);
                            if(QuizConfig.isValidAnswer(answer)){
                                userAnswer.set(answer);
                                answerGiven.set(true);
                                timerThread.interrupt();
                                break;
                            }else{
                                System.out.println("Please enter a number between "+ QuizConfig.getAnswerRange() + " only");
                                System.out.print("Your answer (" + QuizConfig.getAnswerRange()+ "): ");
                            }
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input! Please enter a number (" +QuizConfig.getAnswerRange() +").");
                            System.out.print("Your answer (" +QuizConfig.getAnswerRange() + "): ");
                        }
                    }
                }
            }
        });
        //start both threads
        timerThread.start();
        inputThread.start();

        //Wait for completion
        try{
            inputThread.join();
            timerThread.join(100);
        } catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }

        //Clear timer line
        System.out.print("\r" + " ".repeat(50) + "\r");

        return timeUp ? -1 : userAnswer.get();
    }

    private void displayWelcomeMessage(){
        System.out.println(QuizConfig.WELCOME_MESSAGE);
        System.out.println("==============================");
        System.out.println("Total questions: "+ questions.length);
        System.out.println("Enter "+ QuizConfig.getAnswerRange() + " as your answer.");
        System.out.println("Let's begin!\n");
    }

    /* private int getUserAnswer(){
      /*  System.out.println("Your answer(1-4): ");
        int answer = scanner.nextInt();
        return answer;
        while(true){
            try{
                System.out.println("Your Answer(" + QuizConfig.getAnswerRange()+ "): ");
                String input = scanner.nextLine().trim();

                if(input.isEmpty()){
                    System.out.println("Please enter a number between " + QuizConfig.getAnswerRange() + "!");
                    continue;
                }
                int answer=Integer.parseInt(input);

                if(QuizConfig.isValidAnswer(answer)){
                    return answer;
                }else{
                    System.out.println("Please enter a number between "+ QuizConfig.getAnswerRange()+ " only!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! please enter a number (" + QuizConfig.getAnswerRange() + ").");
            }catch (Exception e){
                System.out.println("Unexpected error: "+ e.getMessage());
            }
        }
    } */

    private void checkAnswer(Question question,int userAnswer){
        if(timeUp){
            System.out.println(QuizConfig.TIME_UP_MESSAGE);
            System.out.println("The correct answer was: "+ question.getCorrectAnswer());
        } else if(userAnswer==question.getCorrectAnswer()){
            System.out.println(QuizConfig.CORRECT_MESSAGE);
            score++;
        } else{
            System.out.println(QuizConfig.INCORRECT_MESSAGE + question.getCorrectAnswer());
        }
        System.out.println("Current score: " + score + "/" + questions.length);
    }

    private void saveScore(){
        try{
            java.nio.file.Path scorePath = java.nio.file.Paths.get(QuizConfig.SCORE_FILE);
            java.util.List<String> allScores = new java.util.ArrayList<>();

            //Read existing scores if file exists
            if(java.nio.file.Files.exists(scorePath)){
                allScores = java.nio.file.Files.readAllLines(scorePath);
            }

            //Add header if file is new
            if(allScores.isEmpty() || !allScores.getFirst().equals(QuizConfig.SCORES_HEADER)){
                allScores.clear();
                allScores.add(QuizConfig.SCORES_HEADER);
            }

            //Add current score with timestamp
            String timestamp = java.time.LocalDateTime.now().format(
                    java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
            String scoreEntry = String.format("Score: %d/%d - %s", score ,questions.length, timestamp);
            allScores.add(scoreEntry);

            //Keep only last 5 scores + header
            while (allScores.size() >QuizConfig.MAX_SCORE_TO_KEEP+1){
                allScores.remove(1); //remove oldest score(keep header)
            }

            //Add high score message if applicable
            int highScore = getHighScore(allScores);
            if (score == questions.length && score >= highScore){
                allScores.add(QuizConfig.HIGH_SCORE_MESSAGE);
            }

            //Write back to file
            java.nio.file.Files.write(scorePath, allScores);
        } catch (Exception e) {
            System.out.println("Could not save scores: " + e.getMessage());
        }
    }

    private int getHighScore(java.util.List<String> scores){
        int highScore = 0;
        for(String line: scores){
            if(line.startsWith("Score: ")){
                try{
                    String[] parts = line.split(" ")[1].split("/");
                    int currentScore = Integer.parseInt(parts[0]);
                    highScore = Math.max(highScore,currentScore);
                }catch (Exception e){
                    //skip malfunction lines
                }
            }
        }
        return highScore;
    }

    private void showFinalResults(){
        System.out.println("\n"+"=".repeat(50));
        System.out.println("QUIZ COMPLETED!");
        System.out.println("=".repeat(50));

        System.out.println("Final Score: "+score+"/"+ questions.length);

        double percentage = (score*100.0)/ questions.length;
        System.out.println("Percentage: "+percentage+"%");

        //Show score history
        displayScoreHistory();

        //Performance feedback
        if(score== questions.length) {
            System.out.println("PERFECT SCORE!");
        }
        else if(score >= questions.length* 0.8){
            System.out.println("Excellent work!");
        }else if(score>= questions.length/2){
            System.out.println("GOOD JOB!");
        }else{
            System.out.println("KEEP LEARNING! YOU WILL GET BETTER!.");
        }
        scanner.close();
    }

    private void displayScoreHistory(){
        try{
            java.nio.file.Path scoresPath = java.nio.file.Paths.get(QuizConfig.SCORE_FILE);
            if(java.nio.file.Files.exists(scoresPath)){
                java.util.List<String> scores = java.nio.file.Files.readAllLines(scoresPath);

                System.out.println("\n Your Recent Scores:");
                for (String scoreLine : scores){
                    System.out.println(" "+ scoreLine);
                }

                int highScore = getHighScore(scores);
                System.out.println("All-time High Score: "+ highScore+ "/" + questions.length);
            }
        }catch (Exception e){
            System.out.println("Could not load score history");
        }
    }

    public static void main(String[] args){
        QuizApp quiz =new QuizApp();
        quiz.startQuiz();
    }
}
