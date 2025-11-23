
/* REQUIREMENTS:
1. Question        :text-string that holds the actual question.
2. Options         : An array of strings that holds the multiple-choice options.
3. Correct Answers :An integer that represents the correct option(1,2,3,4).
 */
public class Question {
    private final String questionText;
    private final String[] options;
    private final int correctAnswer;

    //making a constructor
   public Question(String questionText,String[] options, int correctAnswer){
        this.questionText= questionText;
        this.options = options;
        this.correctAnswer = correctAnswer;
   }

   //using getter and setter to read private variables
    public String getQuestionText(){
       return questionText;
    }

    public String[] getOptions(){
       return options;
    }

    public int getCorrectAnswer(){
       return correctAnswer;
    }

    //creating a method to display questions
    public void displayQuestion(int questionNumber){
       System.out.println("\nQuestion "+ questionNumber+ ": "+getQuestionText());
       System.out.println("options:");
       for(int i=0;i<getOptions().length;i++){
           System.out.println(" "+ options[i]);
       }
    }

    public static Question fromStringArray(String [] lines,int startIndex){
    String questionText =lines[startIndex];
    String [] options = new String[4];
    options[0]= "1. "+ lines[startIndex+1];
    options[1]= "2. "+ lines[startIndex+2];
    options[2]= "3. "+ lines[startIndex+3];
    options[3]= "4. "+ lines[startIndex+4];
    int correctAnswer = Integer.parseInt(lines[startIndex+5]);

    return new Question(questionText , options,correctAnswer);
    }

}
