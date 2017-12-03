package stelztech.youknowehv4.database.quizcard;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class QuizCard {

    private int quizId;
    private String question;
    private String answer;
    private boolean passed;
    private int numFailed;
    private int position;

    public QuizCard(int quizId, String question, String answer, boolean passed, int numFailed, int position) {
        this.quizId = quizId;
        this.question = question;
        this.answer = answer;
        this.passed = passed;
        this.numFailed = numFailed;
        this.position = position;
    }

    public int getQuizId() {
        return quizId;
    }

    public void setQuizId(int quizId) {
        this.quizId = quizId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public boolean isPassed() {
        return passed;
    }

    public void setPassed(boolean passed) {
        this.passed = passed;
    }

    public int getNumFailed() {
        return numFailed;
    }

    public void setNumFailed(int numFailed) {
        this.numFailed = numFailed;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
