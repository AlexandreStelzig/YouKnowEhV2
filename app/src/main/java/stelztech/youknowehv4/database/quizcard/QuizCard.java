package stelztech.youknowehv4.database.quizcard;

/**
 * Created by Alexandre on 4/25/2016.
 */
public class QuizCard {

    private String quizId;
    private String cardId;
    private boolean passed;
    private int numFailed;

    public QuizCard(String quizId, String cardId, boolean passed, int numFailed) {
        this.quizId = quizId;
        this.cardId = cardId;
        this.passed = passed;
        this.numFailed = numFailed;
    }

    public String getQuizId() {
        return quizId;
    }

    public void setQuizId(String quizId) {
        this.quizId = quizId;
    }

    public String getCardId() {
        return cardId;
    }

    public void setCardId(String cardId) {
        this.cardId = cardId;
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
}
